package com.example.korgasadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddPetrolStationData extends AppCompatActivity {

    private EditText editTextPetrolStationName, editTextContact, editTextAddress, editTextWebsiteLink, editTextMapLink;
    private ProgressBar progressBar;

    @Override
    public void onBackPressed() {
        // app icon in action bar clicked; go home
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_petrol_station_data);

        getSupportActionBar().setTitle("Add Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Toast.makeText(this, "You can add Station details now!", Toast.LENGTH_SHORT).show();

        progressBar = findViewById(R.id.idProgressBar);

        editTextPetrolStationName = findViewById(R.id.idTIETPetrolStationName);
        editTextContact = findViewById(R.id.idTIETContact);
        editTextAddress = findViewById(R.id.idTIETAddress);
        editTextWebsiteLink = findViewById(R.id.idTIETWebsiteLink);
        editTextMapLink = findViewById(R.id.idTIETMapLink);

        MaterialButton materialButtonAdd = findViewById(R.id.idBtnAdd);
        materialButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);

//                obtain data enter by user
                String textPetrolStationName = editTextPetrolStationName.getText().toString().trim();
                String textContact = editTextContact.getText().toString().trim();
                String textAddress = editTextAddress.getText().toString().trim();
                String textWebsiteLink = editTextWebsiteLink.getText().toString().trim();
                String textMapLink = editTextMapLink.getText().toString().trim();

                //  mobile number validation. find below
                String mobileRegex = "^(09|\\+639)\\d{9}$";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textContact);

//                Validation of user input
                //  petrol station name validation
                if (textPetrolStationName.isEmpty()){
                    editTextPetrolStationName.setError("Petrol Station Name is required");
                    editTextPetrolStationName.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                //  petrol station birth validation
                else if (textContact.isEmpty()){
                    editTextContact.setError("Petrol Station Birth is required");
                    editTextContact.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                else if (!mobileMatcher.find()){
                    editTextContact.setError("Phone Number should be start at 09");
                    editTextContact.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                else if (textContact.length() !=11){
                    editTextContact.setError("PhoneNumber length should be 11 digits");
                    editTextContact.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                //  petrol station address validation
                else if (textAddress.isEmpty()){
                    editTextAddress.setError("Petrol Station Name is required");
                    editTextAddress.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                //  website link validation
                else if (textWebsiteLink.isEmpty()){
                    editTextWebsiteLink.setError("Petrol Station Website link is required");
                    editTextWebsiteLink.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                else if (!Patterns.WEB_URL.matcher(textWebsiteLink).matches()){
                    editTextWebsiteLink.setError("Petrol Station Website Link is Invalid");
                    editTextWebsiteLink.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                //  website link validation
                else if (textMapLink.isEmpty()){
                    editTextWebsiteLink.setError("Petrol Station Map link is required");
                    editTextWebsiteLink.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                else if (!Patterns.WEB_URL.matcher(textMapLink).matches()){
                    editTextWebsiteLink.setError("Petrol Station Map Link is Invalid");
                    editTextWebsiteLink.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                else {
                    progressBar.setVisibility(View.VISIBLE);

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Petrol Stations Registered").child(firebaseUser.getUid());

                    databaseReference.child("name").setValue(textPetrolStationName);
                    databaseReference.child("contact").setValue(textContact);
                    databaseReference.child("address").setValue(textAddress);
                    databaseReference.child("websiteLink").setValue(textWebsiteLink);
                    databaseReference.child("mapLink").setValue(textMapLink);

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Toast.makeText(AddPetrolStationData.this, "Data added", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddPetrolStationData.this, AddPetrolStationPrice.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
}