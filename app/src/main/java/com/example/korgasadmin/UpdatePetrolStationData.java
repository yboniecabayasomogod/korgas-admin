package com.example.korgasadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdatePetrolStationData extends AppCompatActivity {

    private EditText editTextUpdatePetrolStationName, editTextUpdateContact, editTextUpdateWebsiteLink;
    private TextView textViewUpdateLongitude, textViewUpdateLatitude, textViewUpdateAddress, textViewMapLink;
    private String textPetrolStationName, textContact, textAddress, textLongitude, textLatitude, textWebsiteLink, textMapLink;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;

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
        setContentView(R.layout.activity_update_petrol_station_data);

        getSupportActionBar().setTitle("Update Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.idProgressBar);

        editTextUpdatePetrolStationName = findViewById(R.id.idTIETUpdatePetrolStationName);
        editTextUpdateContact = findViewById(R.id.idTIETUpdateContact);
        editTextUpdateWebsiteLink = findViewById(R.id.idTIETUpdateWebsiteLink);

        textViewMapLink = findViewById(R.id.textViewMapLink);
        textViewUpdateAddress = findViewById(R.id.textViewAddress);
        textViewUpdateLongitude = findViewById(R.id.textViewLongitude);
        textViewUpdateLatitude = findViewById(R.id.textViewLatitude);

        authProfile = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        RelativeLayout relativeLayout = findViewById(R.id.layout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UpdatePetrolStationData.this, "You can't update the address and mapLink, please Contact the Admin for changes", Toast.LENGTH_LONG).show();
            }
        });

        MaterialButton buttonUpdateProfile = findViewById(R.id.idBtnUpdateProfile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });
        showProfile(firebaseUser);
    }

    private void updateProfile(FirebaseUser firebaseUser) {

        //            obtain user data entered
        textPetrolStationName = editTextUpdatePetrolStationName.getText().toString();
        textContact = editTextUpdateContact.getText().toString();
        textWebsiteLink = editTextUpdateWebsiteLink.getText().toString();

        //  mobile number validation. find below
        String mobileRegex = "^(09|\\+639)\\d{9}$";
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(textContact);

        //  petrol station name validation
        if (textPetrolStationName.isEmpty()){
            editTextUpdatePetrolStationName.setError("Petrol Station Name is required");
            editTextUpdatePetrolStationName.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        //  petrol station birth validation
        else if (textContact.isEmpty()){
            editTextUpdateContact.setError("Petrol Station Birth is required");
            editTextUpdateContact.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        else if (!mobileMatcher.find()){
            editTextUpdateContact.setError("Phone Number should be start at 09");
            editTextUpdateContact.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        else if (textContact.length() !=11){
            editTextUpdateContact.setError("PhoneNumber length should be 11 digits");
            editTextUpdateContact.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }


        //  website link validation
        else if (textWebsiteLink.isEmpty()){
            editTextUpdateWebsiteLink.setError("Petrol Station Website link is required");
            editTextUpdateWebsiteLink.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        else if (!Patterns.WEB_URL.matcher(textWebsiteLink).matches()){
            editTextUpdateWebsiteLink.setError("Petrol Station Website Link is Invalid");
            editTextUpdateWebsiteLink.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        else {
            progressBar.setVisibility(View.VISIBLE);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Petrol Stations Registered").child(firebaseUser.getUid());

            databaseReference.child("name").setValue(textPetrolStationName);
            databaseReference.child("contact").setValue(textContact);
            databaseReference.child("websiteLink").setValue(textWebsiteLink);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Toast.makeText(UpdatePetrolStationData.this, "Update Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdatePetrolStationData.this, PetrolStationProfile.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UpdatePetrolStationData.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showProfile(FirebaseUser firebaseUser) {
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Petrol Stations Registered").child(firebaseUser.getUid());

        progressBar.setVisibility(View.VISIBLE);

        referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ReadWritePetrolStationData readWritePetrolStationData =snapshot.getValue(ReadWritePetrolStationData.class);

                if (readWritePetrolStationData != null) {
                    textPetrolStationName = readWritePetrolStationData.name;
                    textContact = readWritePetrolStationData.contact;
                    textAddress = readWritePetrolStationData.address;
                    textLongitude = readWritePetrolStationData.longitude;
                    textLatitude = readWritePetrolStationData.latitude;
                    textWebsiteLink = readWritePetrolStationData.websiteLink;
                    textMapLink = readWritePetrolStationData.mapLink;

                    editTextUpdatePetrolStationName.setText(textPetrolStationName);
                    editTextUpdateContact.setText(textContact);
                    editTextUpdateWebsiteLink.setText(textWebsiteLink);
                    textViewMapLink.setText(textMapLink);
                    textViewUpdateAddress.setText(textAddress);
                    textViewUpdateLongitude.setText(textLongitude);
                    textViewUpdateLatitude.setText(textLatitude);
                }

                else {
                    Toast.makeText(UpdatePetrolStationData.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UpdatePetrolStationData.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(UpdatePetrolStationData.this, "refresh", Toast.LENGTH_SHORT).show();
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);
                break;

            case R.id.menu_post:
                Intent intent0 = new Intent(UpdatePetrolStationData.this, UpdatePetrolStationPost.class);
                startActivity(intent0);
                finish();
                break;

            case R.id.menu_update_petrol_station_price:
                Intent intent1 = new Intent(UpdatePetrolStationData.this, UpdatePetrolStationPrice.class);
                startActivity(intent1);
                finish();
                break;

            case R.id.menu_update_petrol_station_data:
                Intent intent2 = new Intent( UpdatePetrolStationData.this, UpdatePetrolStationData.class);
                startActivity(intent2);
                finish();
                break;

            case R.id.menu_update_petrol_station_email:
                Intent intent3 = new Intent(UpdatePetrolStationData.this, UpdatePetrolStationEmail.class);
                startActivity(intent3);
                finish();
                break;

            case R.id.menu_change_update_petrol_station_password:
                Intent intent4 = new Intent(UpdatePetrolStationData.this, UpdatePetrolStationPassword.class);
                startActivity(intent4);
                finish();
                break;

            case R.id.menu_delete_petrol_station:
                Intent intent5 = new Intent(UpdatePetrolStationData.this, DeletePetrolStation.class);
                startActivity(intent5);
                finish();
                break;

            case R.id.menu_logout:
                authProfile.signOut();
                Toast.makeText(UpdatePetrolStationData.this, "Logged Out", Toast.LENGTH_LONG).show();
                Intent intent6 = new Intent(UpdatePetrolStationData.this, Login.class);
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