package com.example.korgasadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PetrolStationProfile extends AppCompatActivity {

    private TextView textViewPetrolStationName, textViewGasolinePrice, textViewDieselPrice, textViewKerosenePrice, textViewEmail,
            textViewContact, textViewAddress;

    private ProgressBar progressBar1, progressBar2, progressBar3;

    private String gasolinePrice, dieselPrice, kerosenePrice, petrolStationName, email, contact, address, longitude, latitude, websiteLink , petrolStationPicture;

    private SwipeRefreshLayout swipeContainer;

    private ImageView imageView;

    private FirebaseAuth authProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petrol_station_profile);

        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        swipeToRefresh();

        textViewPetrolStationName = findViewById(R.id.textViewPetrolStationName);
        textViewGasolinePrice = findViewById(R.id.textViewGasolinePrice);
        textViewDieselPrice = findViewById(R.id.textViewDieselPrice);
        textViewKerosenePrice = findViewById(R.id.textViewKerosenePrice);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewContact = findViewById(R.id.textViewContact);
        textViewAddress = findViewById(R.id.textViewAddress);

        progressBar1 = findViewById(R.id.idProgressBar1);
        progressBar2 = findViewById(R.id.idProgressBar2);
        progressBar3 = findViewById(R.id.idProgressBar3);

        authProfile  = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        imageView = findViewById(R.id.image_userProfile);

        MaterialButton btnMapLink = findViewById(R.id.idBtnMapLink);
        btnMapLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.streetview:cbll=" + latitude +","+longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        MaterialButton btnWebLink = findViewById(R.id.idBtnWebLink);
        btnWebLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(websiteLink);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.android.chrome");
                startActivity(intent);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PetrolStationProfile.this, UpdatePetrolStationPicture.class);
                startActivity(intent);
            }
        });

        showUserProfile(firebaseUser);

    }

    private void swipeToRefresh() {
        swipeContainer = findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);
                swipeContainer.setRefreshing(false);

            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Petrol Stations Registered");

        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWritePetrolStationData readWritePetrolStationData = snapshot.getValue(ReadWritePetrolStationData.class);

                if (readWritePetrolStationData !=null) {

                    email = firebaseUser.getEmail();
                    petrolStationName = readWritePetrolStationData.name;
                    longitude = readWritePetrolStationData.longitude;
                    latitude = readWritePetrolStationData.latitude;
                    contact = readWritePetrolStationData.contact;
                    address = readWritePetrolStationData.address;
                    websiteLink = readWritePetrolStationData.websiteLink;
                    gasolinePrice = readWritePetrolStationData.gasolinePrice;
                    dieselPrice = readWritePetrolStationData.dieselPrice;
                    kerosenePrice = readWritePetrolStationData.kerosenePrice;
                    petrolStationPicture = readWritePetrolStationData.petrolStationPicture;


                    textViewPetrolStationName.setText("Welcome. " + petrolStationName + "!");
                    textViewEmail.setText(email);
                    textViewContact.setText(contact);
                    textViewAddress.setText(address);
                    textViewGasolinePrice.setText(gasolinePrice);
                    textViewDieselPrice.setText(dieselPrice);
                    textViewKerosenePrice.setText(kerosenePrice);
                    Picasso.get().load(petrolStationPicture).into(imageView);

                }
                else {
                    authProfile.signOut();
                    Toast.makeText(PetrolStationProfile.this, "Something wrong no user details found", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(PetrolStationProfile.this, Login.class);
                    startActivity(intent);

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                progressBar1.setVisibility(View.GONE);
                progressBar2.setVisibility(View.GONE);
                progressBar3.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(PetrolStationProfile.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                progressBar1.setVisibility(View.GONE);
                progressBar2.setVisibility(View.GONE);
                progressBar3.setVisibility(View.GONE);
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
                Toast.makeText(PetrolStationProfile.this, "refresh", Toast.LENGTH_SHORT).show();
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);
                break;

            case R.id.menu_post:
                Intent intent0 = new Intent(PetrolStationProfile.this, UpdatePetrolStationPost.class);
                startActivity(intent0);
                finish();
                break;

            case R.id.menu_update_petrol_station_price:
                Intent intent1 = new Intent(PetrolStationProfile.this, UpdatePetrolStationPrice.class);
                startActivity(intent1);
                finish();
                break;

            case R.id.menu_update_petrol_station_data:
                Intent intent2 = new Intent( PetrolStationProfile.this, UpdatePetrolStationData.class);
                startActivity(intent2);
                finish();
                break;

            case R.id.menu_update_petrol_station_email:
                Intent intent3 = new Intent(PetrolStationProfile.this, UpdatePetrolStationEmail.class);
                startActivity(intent3);
                finish();
                break;

            case R.id.menu_change_update_petrol_station_password:
                Intent intent4 = new Intent(PetrolStationProfile.this, UpdatePetrolStationPassword.class);
                startActivity(intent4);
                finish();
                break;

            case R.id.menu_delete_petrol_station:
                Intent intent5 = new Intent(PetrolStationProfile.this, DeletePetrolStation.class);
                startActivity(intent5);
                finish();
                break;

            case R.id.menu_logout:
                authProfile.signOut();
                Toast.makeText(PetrolStationProfile.this, "Logged Out", Toast.LENGTH_LONG).show();
                Intent intent6 = new Intent(PetrolStationProfile.this, Login.class);
                startActivity(intent6);

                intent6.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent6);
                finish();
//            close user profile
                break;

            default:
//                Toast.makeText(UserProfileActivity.this, "Something wrong!", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}