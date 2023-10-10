package com.example.korgasadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPassword extends AppCompatActivity {

    private MaterialButton forgotPasswordBtn;
    private EditText emailPasswordReset;
    private ProgressBar progressBar;

    private FirebaseAuth authProfile;

    private final static String TAG = "ForgotPasswordActivity";

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().setTitle("Password Reset");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailPasswordReset = findViewById(R.id.idTIETEmail);

        progressBar = findViewById(R.id.idProgressBar);

        forgotPasswordBtn = findViewById(R.id.idBtnForgotPassword);

        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailPasswordReset.getText().toString();

                //email validation
                if (email.isEmpty()){
                    emailPasswordReset.setError("Email is required");
                    emailPasswordReset.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailPasswordReset.setError("Please provide a valid email");
                    emailPasswordReset.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                else {
                    progressBar.setVisibility(View.VISIBLE);
                    resetPassword(email);
                }

            }
        });

    }
    //reset password process
    private void resetPassword(String email) {
        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this, "Please check your email inbox for password reset link!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ForgotPassword.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                else {

                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidUserException e) {
                        emailPasswordReset.setError("User does not exist or no longer valid. Please register again.");
                        progressBar.setVisibility(View.GONE);
                    }
                    catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(ForgotPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}