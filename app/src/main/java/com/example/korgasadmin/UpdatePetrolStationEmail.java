package com.example.korgasadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePetrolStationEmail extends AppCompatActivity {

    private FirebaseAuth authProfile;

    private FirebaseUser firebaseUser;

    private ProgressBar progressBar1, progressBar2;
    private TextView textViewAuthenticated;
    private String userOldEmail, userNewEmail, userPwd;
    private Button buttonUpdateEmail;
    private EditText editTextNewEmail, editTextPassword, editTextOldEmail;

    //    disable physical back button
    @Override
    public void onBackPressed() {
        // app icon in action bar clicked; go home
        Intent intent = new Intent(this, PetrolStationProfile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_petrol_station_email);

        getSupportActionBar().setTitle("Update Email");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar1 = findViewById(R.id.idProgressBar1);
        progressBar2 = findViewById(R.id.idProgressBar2);
        editTextPassword = findViewById(R.id.idTIETPassword);
        editTextNewEmail = findViewById(R.id.idTIETUpdateEmail);
        textViewAuthenticated = findViewById(R.id.subTitle_emailAuthenticate);
        buttonUpdateEmail = findViewById(R.id.idBtnUpdateEmail);
        editTextOldEmail = findViewById(R.id.idTIETOldEmail);

//        make button disbaled in the beggining until the user input and verified
        buttonUpdateEmail.setEnabled(false);
        editTextNewEmail.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

//        set old email on texView
        userOldEmail = firebaseUser.getEmail();
        TextView textViewOldEmail = findViewById(R.id.idTIETOldEmail);
        textViewOldEmail.setText(userOldEmail);

        if (firebaseUser==null){
            Toast.makeText(UpdatePetrolStationEmail.this, "Something went wrong!", Toast.LENGTH_LONG).show();
        }
        else {
            reAuthenticate(firebaseUser);
        }
    }

    private void reAuthenticate(FirebaseUser firebaseUser) {

        Button buttonVerifyUser = findViewById(R.id.idBtnAuthenticate);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPwd = editTextPassword.getText().toString();

                if (userPwd.isEmpty()){
                    editTextPassword.setError("Password is required to continue to update email");
                    editTextPassword.requestFocus();
                    return;
                }
                else {
                    progressBar1.setVisibility(View.VISIBLE);
                    progressBar2.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail, userPwd);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                progressBar1.setVisibility(View.GONE);
                                progressBar2.setVisibility(View.GONE);

                                Toast.makeText(UpdatePetrolStationEmail.this, "Password has been verified." + "You can update email now!", Toast.LENGTH_SHORT).show();

                                textViewAuthenticated.setText("You are authenticated. You can update your emails address");

                                editTextPassword.setEnabled(false);
                                buttonVerifyUser.setEnabled(false);
                                editTextOldEmail.setEnabled(false);
                                editTextNewEmail.setEnabled(true);
                                buttonUpdateEmail.setEnabled(true);

                                buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdatePetrolStationEmail.this, R.color.teal_700));

                                buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        userNewEmail = editTextNewEmail.getText().toString();

                                        if (userNewEmail.isEmpty()) {
                                            editTextNewEmail.setError("Email is required");
                                            editTextNewEmail.requestFocus();
                                            progressBar1.setVisibility(View.GONE);
                                            progressBar2.setVisibility(View.GONE);
                                            return;
                                        }
                                        else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()){
                                            editTextNewEmail.setError("Please provide a valid email");
                                            editTextNewEmail.requestFocus();
                                            progressBar1.setVisibility(View.GONE);
                                            progressBar2.setVisibility(View.GONE);
                                            return;
                                        }
                                        else if (userOldEmail.matches(userNewEmail)){
                                            Toast.makeText(UpdatePetrolStationEmail.this, "New email cannot be same old Email", Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("Please enter new Email");
                                            editTextNewEmail.requestFocus();
                                            return;
                                        }
                                        else {
                                            progressBar1.setVisibility(View.VISIBLE);
                                            progressBar2.setVisibility(View.VISIBLE);

                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });
                            }
                            else {
                                try {
                                    throw task.getException();
                                }
                                catch (Exception e){
                                    Toast.makeText(UpdatePetrolStationEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar1.setVisibility(View.GONE);
                                    progressBar2.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
//                    verify email
                    firebaseUser.sendEmailVerification();

                    Toast.makeText(UpdatePetrolStationEmail.this, "Email has been updated. PLease verify your new email", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdatePetrolStationEmail.this, PetrolStationProfile.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    try {
                        throw task.getException();
                    }
                    catch (Exception e) {
                        Toast.makeText(UpdatePetrolStationEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar1.setVisibility(View.GONE);
                progressBar2.setVisibility(View.GONE);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, PetrolStationProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;

            case R.id.menu_refresh:
                Toast.makeText(UpdatePetrolStationEmail.this, "refresh", Toast.LENGTH_SHORT).show();
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);
                break;

            case R.id.menu_post:
                Intent intent0 = new Intent(UpdatePetrolStationEmail.this, UpdatePetrolStationPost.class);
                startActivity(intent0);
                finish();
                break;

            case R.id.menu_update_petrol_station_price:
                Intent intent1 = new Intent(UpdatePetrolStationEmail.this, UpdatePetrolStationPrice.class);
                startActivity(intent1);
                finish();
                break;

            case R.id.menu_update_petrol_station_data:
                Intent intent2 = new Intent( UpdatePetrolStationEmail.this, UpdatePetrolStationData.class);
                startActivity(intent2);
                finish();
                break;

            case R.id.menu_update_petrol_station_email:
                Intent intent3 = new Intent(UpdatePetrolStationEmail.this, UpdatePetrolStationEmail.class);
                startActivity(intent3);
                finish();
                break;

            case R.id.menu_change_update_petrol_station_password:
                Intent intent4 = new Intent(UpdatePetrolStationEmail.this, UpdatePetrolStationPassword.class);
                startActivity(intent4);
                finish();
                break;

            case R.id.menu_delete_petrol_station:
                Intent intent5 = new Intent(UpdatePetrolStationEmail.this, DeletePetrolStation.class);
                startActivity(intent5);
                finish();
                break;

            case R.id.menu_logout:
                authProfile.signOut();
                Toast.makeText(UpdatePetrolStationEmail.this, "Logged Out", Toast.LENGTH_LONG).show();
                Intent intent6 = new Intent(UpdatePetrolStationEmail.this, Login.class);
                startActivity(intent6);

                intent6.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent6);
                finish();
                break;

            default:
        }
        return super.onOptionsItemSelected(item);
    }
}