package com.example.korgasadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeletePetrolStation extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;

    private EditText editTextUserPwd;
    private TextView textViewAuthenticated;
    private ProgressBar progressBar1, progressBar2;
    private String userPwd;
    private MaterialButton buttonAuthenticate, buttonDeleteUser;
    private  static final String TAG="DeleteProfileActivity";

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
        setContentView(R.layout.activity_delete_petrol_station);

        getSupportActionBar().setTitle("Delete Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        progressBar1 = findViewById(R.id.idProgressBar1);
        progressBar2 = findViewById(R.id.idProgressBar2);
        textViewAuthenticated = findViewById(R.id.subTitle_deleteUserAuthenticated);
        buttonAuthenticate = findViewById(R.id.idBtnAuthenticate_deleteUser);
        buttonDeleteUser = findViewById(R.id.idBtnDeleteUser);
        editTextUserPwd = findViewById(R.id.idTIETDeleteUser);

        buttonDeleteUser.setEnabled(false);

        if (firebaseUser.equals("")) {
            Toast.makeText(DeletePetrolStation.this, "Something went wrong" + "User details are not available at the moment", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DeletePetrolStation.this, PetrolStationProfile.class);
            startActivity(intent);
            finish();
        }
        else {
            reAuthenticateUser(firebaseUser);
        }
    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPwd = editTextUserPwd.getText().toString();

                //  password validation
                if (userPwd.isEmpty()){
                    editTextUserPwd.setError("Password is required");
                    editTextUserPwd.requestFocus();
                    progressBar1.setVisibility(View.GONE);
                    progressBar2.setVisibility(View.GONE);
                    return;
                }

                else if (userPwd.length()<6){
                    editTextUserPwd.setError("Password length should be 6 character");
                    editTextUserPwd.requestFocus();
                    progressBar1.setVisibility(View.GONE);
                    progressBar2.setVisibility(View.GONE);
                    return;
                }
                else {
                    progressBar1.setVisibility(View.VISIBLE);
                    progressBar2.setVisibility(View.VISIBLE);

//                    ReAuthenticate user now
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwd);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBar1.setVisibility(View.GONE);
                                progressBar2.setVisibility(View.GONE);

                                editTextUserPwd.setEnabled(false);
                                buttonAuthenticate.setEnabled(false);
//                             button authenticate dibble and button delete is enable
                                buttonDeleteUser.setEnabled(true);

                                textViewAuthenticated.setText("You are authenticated/verified" + " You can change your password now!");

                                Toast.makeText(DeletePetrolStation.this, "Password has been verified" + "Change password now", Toast.LENGTH_SHORT).show();

                                buttonDeleteUser.setBackgroundTintList(ContextCompat.getColorStateList(DeletePetrolStation.this, R.color.red));

                                buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showAlertDialog();
                                    }
                                });
                            }
                            else {
                                try {
                                    throw task.getException();
                                }
                                catch (Exception e){
                                    Toast.makeText(DeletePetrolStation.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        });
    }

    private void showAlertDialog() {
        //        setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(DeletePetrolStation.this);
        builder.setTitle("Delete User and related data");
        builder.setMessage("Do you want to delete your profile and related data? this action is irreversible!");

//        open email app if user click the continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUserData(firebaseUser);
            }
        });
//        Return to user profile activity is user presses can cel button

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(DeletePetrolStation.this, PetrolStationProfile.class);
                startActivity(intent);
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
            }
        });
        alertDialog.show();
    }
    private void deleteUserData(FirebaseUser firebaseUser) {

        if (firebaseUser.getPhotoUrl() != null) {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReferenceFromUrl(firebaseUser.getPhotoUrl().toString());
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(TAG, "OnSuccess: photo deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, e.getMessage());
                    Toast.makeText(DeletePetrolStation.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Petrol Stations Registered");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "OnSuccess: User Data Deleted");
                deleteUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.getMessage());
                Toast.makeText(DeletePetrolStation.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUser() {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    authProfile.signOut();
                    Toast.makeText(DeletePetrolStation.this, "User has been deleted", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(DeletePetrolStation.this, Login.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    try {
                        throw task.getException();
                    }
                    catch (Exception e) {
                        Toast.makeText(DeletePetrolStation.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DeletePetrolStation.this, "refresh", Toast.LENGTH_SHORT).show();
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);
                break;

            case R.id.menu_post:
                Intent intent0 = new Intent(DeletePetrolStation.this, UpdatePetrolStationPost.class);
                startActivity(intent0);
                finish();
                break;

            case R.id.menu_update_petrol_station_price:
                Intent intent1 = new Intent(DeletePetrolStation.this, UpdatePetrolStationPrice.class);
                startActivity(intent1);
                finish();
                break;

            case R.id.menu_update_petrol_station_data:
                Intent intent2 = new Intent( DeletePetrolStation.this, UpdatePetrolStationData.class);
                startActivity(intent2);
                finish();
                break;

            case R.id.menu_update_petrol_station_email:
                Intent intent3 = new Intent(DeletePetrolStation.this, UpdatePetrolStationEmail.class);
                startActivity(intent3);
                finish();
                break;

            case R.id.menu_change_update_petrol_station_password:
                Intent intent4 = new Intent(DeletePetrolStation.this, UpdatePetrolStationPassword.class);
                startActivity(intent4);
                finish();
                break;

            case R.id.menu_delete_petrol_station:
                Intent intent5 = new Intent(DeletePetrolStation.this, DeletePetrolStation.class);
                startActivity(intent5);
                finish();
                break;

            case R.id.menu_logout:
                authProfile.signOut();
                Toast.makeText(DeletePetrolStation.this, "Logged Out", Toast.LENGTH_LONG).show();
                Intent intent6 = new Intent(DeletePetrolStation.this, Login.class);
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