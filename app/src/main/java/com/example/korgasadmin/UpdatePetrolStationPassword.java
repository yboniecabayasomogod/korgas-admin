package com.example.korgasadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePetrolStationPassword extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private EditText editTextPwdCurr, editTextPwdNew, editTextPwdConfirmNew;
    private TextView textViewAuthenticated;
    private MaterialButton buttonChangePassword, buttonReAuthenticate;
    private ProgressBar progressBar1, progressBar2;
    private String userPwdCurr;

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
        setContentView(R.layout.activity_update_petrol_station_password);

        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        editTextPwdNew = findViewById(R.id.idTIETChangePassword);
        editTextPwdCurr = findViewById(R.id.idTIETCurrentPassword);
        editTextPwdConfirmNew = findViewById(R.id.idTIETMatchChangePassword);
        textViewAuthenticated = findViewById(R.id.subTitle_changePasswordAuthenticate);
        progressBar1 = findViewById(R.id.idProgressBar1);
        progressBar2 = findViewById(R.id.idProgressBar2);
        buttonReAuthenticate = findViewById(R.id.idBtnAuthenticate_currentPassword);
        buttonChangePassword = findViewById(R.id.idBtnUpdatePassword);

        editTextPwdNew.setEnabled(false);
        editTextPwdConfirmNew.setEnabled(false);
        buttonChangePassword.setEnabled(false);

        if (firebaseUser.equals("")) {
            Toast.makeText(UpdatePetrolStationPassword.this, "Something went wrong! User's details not available", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdatePetrolStationPassword.this, PetrolStationProfile.class);
            startActivity(intent);
            finish();
        }
        else {
            reAuthenticateUser(firebaseUser);
        }
    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPwdCurr = editTextPwdCurr.getText().toString();

                if (userPwdCurr.isEmpty()){
                    editTextPwdCurr.setError("Password is required");
                    editTextPwdCurr.requestFocus();
                    progressBar1.setVisibility(View.GONE);
                    progressBar2.setVisibility(View.GONE);
                    return;
                }

                else if (userPwdCurr.length()<6){
                    editTextPwdCurr.setError("Password length should be 6 character");
                    editTextPwdCurr.requestFocus();
                    progressBar1.setVisibility(View.GONE);
                    progressBar2.setVisibility(View.GONE);
                    return;
                }
                else {
                    progressBar1.setVisibility(View.VISIBLE);
                    progressBar2.setVisibility(View.VISIBLE);

//                    ReAuthenticate user now
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwdCurr);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBar1.setVisibility(View.GONE);
                                progressBar2.setVisibility(View.GONE);

//                             disable edittext for current password. enable edittext for now and confirm new password
                                editTextPwdCurr.setEnabled(false);
                                buttonReAuthenticate.setEnabled(false);

//                             button authenticate disable and button swordsmanship is enable
                                editTextPwdNew.setEnabled(true);
                                editTextPwdConfirmNew.setEnabled(true);
                                buttonChangePassword.setEnabled(true);

//                             set textview to show user is authenticated/verified
                                textViewAuthenticated.setText("You are authenticated/verified" + " You can change your password now!");

                                Toast.makeText(UpdatePetrolStationPassword.this, "Password has been verified" + "Change password now", Toast.LENGTH_SHORT).show();

//                             Change password button
                                buttonChangePassword.setBackgroundTintList(ContextCompat.getColorStateList(UpdatePetrolStationPassword.this, R.color.teal_700));

                                buttonChangePassword.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        changePwd(firebaseUser);
                                    }
                                });
                            }
                            else {
                                try {
                                    throw task.getException();
                                }
                                catch (Exception e){
                                    Toast.makeText(UpdatePetrolStationPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar1.setVisibility(View.GONE);
                            progressBar2.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew = editTextPwdNew.getText().toString();
        String userPwdConfirmNew = editTextPwdConfirmNew.getText().toString();

        //  password validation
        if (userPwdNew.isEmpty()){
            editTextPwdNew.setError("Password is required");
            editTextPwdNew.requestFocus();
            progressBar1.setVisibility(View.GONE);
            progressBar2.setVisibility(View.GONE);
            return;
        }

        else if (userPwdNew.length()<6){
            editTextPwdNew.setError("Password length should be 6 character");
            editTextPwdNew.requestFocus();
            progressBar1.setVisibility(View.GONE);
            progressBar2.setVisibility(View.GONE);
            return;
        }
        //  confirm password validation
        else if (userPwdConfirmNew.isEmpty()){
            editTextPwdConfirmNew.setError("Confirm your password Password");
            editTextPwdConfirmNew.requestFocus();
            progressBar1.setVisibility(View.GONE);
            progressBar2.setVisibility(View.GONE);
            return;
        }

        else if (userPwdConfirmNew.length()<6){
            editTextPwdConfirmNew.setError("password length should be 6 character");
            editTextPwdConfirmNew.requestFocus();
            progressBar1.setVisibility(View.GONE);
            progressBar2.setVisibility(View.GONE);
            return;
        }
        //  confirm if password and confirm password are equal
        else if (!userPwdNew.equals(userPwdConfirmNew)) {
            Toast.makeText(UpdatePetrolStationPassword.this, "Please check both having same password..", Toast.LENGTH_SHORT).show();
//                    clear password entered
            editTextPwdNew.clearComposingText();
            editTextPwdConfirmNew.clearComposingText();
            progressBar1.setVisibility(View.GONE);
            progressBar2.setVisibility(View.GONE);
            return;
        }

        //  confirm if password and confirm password are equal
        else if (userPwdCurr.equals(userPwdNew)) {
            Toast.makeText(UpdatePetrolStationPassword.this, "Password entered not be the same in old one..", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("password is them same from old");
            editTextPwdNew.requestFocus();
            progressBar1.setVisibility(View.GONE);
            progressBar2.setVisibility(View.GONE);
            return;
        }
        else {
            progressBar1.setVisibility(View.GONE);
            progressBar2.setVisibility(View.GONE);

            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UpdatePetrolStationPassword.this, "Password has been changed!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdatePetrolStationPassword.this, PetrolStationProfile.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        try {
                            throw task.getException();
                        }
                        catch (Exception e){
                            Toast.makeText(UpdatePetrolStationPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar1.setVisibility(View.GONE);
                            progressBar2.setVisibility(View.GONE);
                        }
                    }
                    progressBar1.setVisibility(View.GONE);
                    progressBar2.setVisibility(View.GONE);
                }
            });
        }
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
                Toast.makeText(UpdatePetrolStationPassword.this, "refresh", Toast.LENGTH_SHORT).show();
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);
                break;

            case R.id.menu_post:
                Intent intent0 = new Intent(UpdatePetrolStationPassword.this, UpdatePetrolStationPost.class);
                startActivity(intent0);
                finish();
                break;

            case R.id.menu_update_petrol_station_price:
                Intent intent1 = new Intent(UpdatePetrolStationPassword.this, UpdatePetrolStationPrice.class);
                startActivity(intent1);
                finish();
                break;

            case R.id.menu_update_petrol_station_data:
                Intent intent2 = new Intent( UpdatePetrolStationPassword.this, UpdatePetrolStationData.class);
                startActivity(intent2);
                finish();
                break;

            case R.id.menu_update_petrol_station_email:
                Intent intent3 = new Intent(UpdatePetrolStationPassword.this, UpdatePetrolStationEmail.class);
                startActivity(intent3);
                finish();
                break;

            case R.id.menu_change_update_petrol_station_password:
                Intent intent4 = new Intent(UpdatePetrolStationPassword.this, UpdatePetrolStationPassword.class);
                startActivity(intent4);
                finish();
                break;

            case R.id.menu_delete_petrol_station:
                Intent intent5 = new Intent(UpdatePetrolStationPassword.this, DeletePetrolStation.class);
                startActivity(intent5);
                finish();
                break;

            case R.id.menu_logout:
                authProfile.signOut();
                Toast.makeText(UpdatePetrolStationPassword.this, "Logged Out", Toast.LENGTH_LONG).show();
                Intent intent6 = new Intent(UpdatePetrolStationPassword.this, Login.class);
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