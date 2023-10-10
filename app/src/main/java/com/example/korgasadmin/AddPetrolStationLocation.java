package com.example.korgasadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddPetrolStationLocation extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    TextView address, city, country, latitude, longitude;
    MaterialButton materialButtonAddLocation, materialButtonGetLocation;
    ProgressBar progressBar;

    private final  static  int REQUEST_CODE = 100;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AddPetrolStationPrice.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_petrol_station_location);

        address=findViewById(R.id.idAddress);
        city=findViewById(R.id.idCity);
        country=findViewById(R.id.idCountry);
        latitude=findViewById(R.id.idLatitude);
        longitude=findViewById(R.id.idLongitude);
        progressBar = findViewById(R.id.idProgressBar);
        materialButtonAddLocation = findViewById(R.id.addLocation);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        materialButtonGetLocation = findViewById(R.id.getLocation);
        materialButtonGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
            }
        });


        materialButtonAddLocation.setEnabled(false);
        materialButtonAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);

                String textLatitude = latitude.getText().toString().trim();
                String textLongitude = longitude.getText().toString().trim();

                if (textLatitude.isEmpty()) {
                    latitude.setError("No Latitude found");
                    progressBar.setVisibility(View.GONE);
                    latitude.requestFocus();
                    return;
                }

                else if (textLongitude.isEmpty()) {
                    longitude.setError("No Longitude found");
                    progressBar.setVisibility(View.GONE);
                    longitude.requestFocus();
                    return;
                }

                else {
                    progressBar.setVisibility(View.VISIBLE);
                    addLocation (textLatitude, textLongitude);
                }

            }
        });
    }

    private void addLocation(String textLatitude, String textLongitude) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Petrol Stations Registered").child(firebaseUser.getUid());

        databaseReference.child("latitude").setValue(textLatitude);
        databaseReference.child("longitude").setValue(textLongitude);
        databaseReference.child("petrolStationPicture").setValue("https://firebasestorage.googleapis.com/v0/b/korgas-admin-8e596.appspot.com/o/Admin%20images%2Fno%20image.png?alt=media&token=18a321b7-41e8-41d3-bb87-e4330c655d56");
        databaseReference.child("postSaySomething").setValue("");
        databaseReference.child("postSomethingPicture").setValue("https://firebasestorage.googleapis.com/v0/b/korgas-admin-8e596.appspot.com/o/Admin%20images%2Fno%20image.png?alt=media&token=18a321b7-41e8-41d3-bb87-e4330c655d56");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(AddPetrolStationLocation.this, "Location added", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddPetrolStationLocation.this, PetrolStationProfile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getLastLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){

            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(android.location.Location location) {

                    if (location!=null){
                        Geocoder geocoder = new Geocoder(AddPetrolStationLocation.this, Locale.getDefault());
                        List<Address> addresses= null;
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);

                            materialButtonAddLocation.setEnabled(true);
                            materialButtonAddLocation.setBackgroundTintList(ContextCompat.getColorStateList(AddPetrolStationLocation.this, R.color.teal_700));

                            latitude.setText("Latitude:  "+ addresses.get(0).getLatitude());
                            longitude.setText("Longitude:  "+ addresses.get(0).getLongitude());
                            address.setText("Address: "+ addresses.get(0).getAddressLine(0));
                            city.setText("Address: "+ addresses.get(0).getLocality());
                            country.setText("Address: "+ addresses.get(0).getCountryName());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        else {

            askPermission();
        }
    }
    private void askPermission() {

        ActivityCompat.requestPermissions(AddPetrolStationLocation.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
            else {
                Toast.makeText(this, "Required Permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}