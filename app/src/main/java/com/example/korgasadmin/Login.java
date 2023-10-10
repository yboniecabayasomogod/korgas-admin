package com.example.korgasadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;

    private FirebaseAuth authProfile;

    private static final String TAG = "LoginActivity";

    Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Toast.makeText(Login.this, "You can login now", Toast.LENGTH_SHORT).show();

        myDialog = new Dialog(this);

        editTextEmail = findViewById(R.id.idTIETEmail);
        editTextPassword = findViewById(R.id.idTIETPassword);

        progressBar = findViewById(R.id.idProgressBar);

        authProfile = FirebaseAuth.getInstance();

        TextView textViewForgotPassword = findViewById(R.id.idTVForGotPassword);
        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this, "You can reset password now!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Login.this, ForgotPassword.class));
            }
        });

        TextView textViewShowHidePassword = findViewById(R.id.idShowPassword);
        textViewShowHidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    textViewShowHidePassword.setText("show");
                }
                else {
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    textViewShowHidePassword.setText("hide");

                }
            }
        });

//        login user
        MaterialButton loginButton = findViewById(R.id.idBtnLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                progressBar.setVisibility(View.VISIBLE);

                if (email.isEmpty()) {
                    editTextEmail.setError("Email is required");
                    editTextEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    editTextEmail.setError("Please provide a valid email");
                    editTextEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                //password validation
                else if (password.isEmpty()){
                    editTextPassword.setError("Password is required");
                    editTextPassword.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(email, password);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        authProfile.signInWithEmailAndPassword(email, password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();

                    if (firebaseUser.isEmailVerified()) {
                        progressBar.setVisibility(View.VISIBLE);

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Petrol Stations Registered").child(firebaseUser.getUid());

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                ReadWritePetrolStationData readWritePetrolStationData = snapshot.getValue(ReadWritePetrolStationData.class);

                                if ( readWritePetrolStationData != null) {
                                    Intent intent = new Intent(Login.this, PetrolStationProfile.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    Toast.makeText(Login.this, "No Data found yet!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Login.this, AddPetrolStationData.class);
                                    startActivity(intent);
                                    finish();
                                }
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else {

                        firebaseUser.sendEmailVerification();
//                        signOut user below this line
                        authProfile.signOut();
                        showAlertDialog();
                    }
                }
                else {
                    try {
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidUserException e) {
                        editTextEmail.setError("user does not exist or is no longer valid. please register again.");
                        editTextEmail.requestFocus();
                        progressBar.setVisibility(View.GONE);
                    }
                    catch (FirebaseAuthInvalidCredentialsException e) {
                        editTextEmail.setError("Invalid credential. kindly, check and re-enter.");
                        editTextEmail.requestFocus();
                        progressBar.setVisibility(View.GONE);
                    }
                    catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {
//        setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle("Email is not verified");
        builder.setMessage("Please verify your email now. You can not login without email verification");

//        open email app if user click the continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
//                To email app in new windows and not within our app
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
//        create the AlertDialog
        AlertDialog alertDialog = builder.create();

//        show the alertDialog
        alertDialog.show();
    }

    public void ShowPopup(View v) {
        TextView txtClose;
        TextView textViewFollow;
        myDialog.setContentView(R.layout.popup_register_layout);
        textViewFollow = (TextView) myDialog.findViewById(R.id.idTVFacebook);
        textViewFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.facebook.com/ybonie.somogod.94");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        txtClose =(TextView) myDialog.findViewById(R.id.txtClose);
        txtClose.setText("X");
        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    //check if user is already logged in or not. In such case, straight take the user to the user's profile
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser checkUserIfAlreadyLogin = authProfile.getCurrentUser();

        if (checkUserIfAlreadyLogin !=null) {

            Toast.makeText(Login.this, "Your are already logged in!", Toast.LENGTH_LONG).show();

//            start the  UserProfileActivity and start user profile activity
            startActivity(new Intent(Login.this, PetrolStationProfile.class));
            finish();
        }
        else {

        }
    }

}