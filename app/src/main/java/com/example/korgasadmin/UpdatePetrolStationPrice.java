package com.example.korgasadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdatePetrolStationPrice extends AppCompatActivity {

    private EditText editTextUpdateGasolinePrice, editTextUpdateDieselPrice, editTextUpdateKerosenePrice;
    private String textGasolinePrice, textDieselPrice, textKerosenePrice;
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
        setContentView(R.layout.activity_update_petrol_product_price);

        getSupportActionBar().setTitle("Update Petrol Price");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.idProgressBar);

        editTextUpdateGasolinePrice = findViewById(R.id.idTIETUpdateProductGasolinePrice);
        editTextUpdateDieselPrice = findViewById(R.id.idTIETUpdateProductDieselPrice);
        editTextUpdateKerosenePrice = findViewById(R.id.idTIETUpdateProductKerosenePrice);

        authProfile = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        showPrice(firebaseUser);

        MaterialButton buttonUpdatePetrolProductPrice = findViewById(R.id.idBtnProductPriceUpdate);
        buttonUpdatePetrolProductPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePrice(firebaseUser);
            }
        });
    }

    private void showPrice(FirebaseUser firebaseUser) {

        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Petrol Stations Registered").child(firebaseUser.getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWritePetrolStationData readWritePetrolStationData =snapshot.getValue(ReadWritePetrolStationData.class);

                if (readWritePetrolStationData != null) {
                    textGasolinePrice = readWritePetrolStationData.gasolinePrice;
                    textDieselPrice = readWritePetrolStationData.dieselPrice;
                    textKerosenePrice = readWritePetrolStationData.kerosenePrice;

                    editTextUpdateGasolinePrice.setText(textGasolinePrice);
                    editTextUpdateDieselPrice.setText(textDieselPrice);
                    editTextUpdateKerosenePrice.setText(textKerosenePrice);
                }

                else {
                    Toast.makeText(UpdatePetrolStationPrice.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UpdatePetrolStationPrice.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updatePrice(FirebaseUser firebaseUser) {

        textGasolinePrice = editTextUpdateGasolinePrice.getText().toString();
        textDieselPrice = editTextUpdateDieselPrice.getText().toString();
        textKerosenePrice = editTextUpdateKerosenePrice.getText().toString();

        //  Gasoline Price validation
        if (textGasolinePrice.isEmpty()){
            editTextUpdateGasolinePrice.setError("Gasoline Price?");
            editTextUpdateGasolinePrice.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        else if (!textGasolinePrice.contains(".")) {
            editTextUpdateGasolinePrice.setError("Please Input a Decimal point(.) like 99.99");
            editTextUpdateGasolinePrice.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        //  diesel price validation
        else if (textDieselPrice.isEmpty()){
            editTextUpdateDieselPrice.setError("Diesel Price?");
            editTextUpdateDieselPrice.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        //  diesel price validation
        else if (!textDieselPrice.contains(".")){
            editTextUpdateDieselPrice.setError("Please Input a Decimal point(.) like 99.99");
            editTextUpdateDieselPrice.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        //  kerosene Price validation
        else if (textKerosenePrice.isEmpty()){
            editTextUpdateKerosenePrice.setError("Kerosene Price?");
            editTextUpdateKerosenePrice.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        //  diesel price validation
        else if (!textKerosenePrice.contains(".")){
            editTextUpdateKerosenePrice.setError("Please Input a Decimal point(.) like 99.99");
            editTextUpdateKerosenePrice.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        else {
            progressBar.setVisibility(View.VISIBLE);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Petrol Stations Registered").child(firebaseUser.getUid());

            databaseReference.child("gasolinePrice").setValue(textGasolinePrice);
            databaseReference.child("dieselPrice").setValue(textDieselPrice);
            databaseReference.child("kerosenePrice").setValue(textKerosenePrice);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Toast.makeText(UpdatePetrolStationPrice.this, "Update Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdatePetrolStationPrice.this, PetrolStationProfile.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UpdatePetrolStationPrice.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(UpdatePetrolStationPrice.this, "refresh", Toast.LENGTH_SHORT).show();
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);
                break;

            case R.id.menu_post:
                Intent intent0 = new Intent(UpdatePetrolStationPrice.this, UpdatePetrolStationPost.class);
                startActivity(intent0);
                finish();
                break;

            case R.id.menu_update_petrol_station_price:
                Intent intent1 = new Intent(UpdatePetrolStationPrice.this, UpdatePetrolStationPrice.class);
                startActivity(intent1);
                finish();
                break;

            case R.id.menu_update_petrol_station_data:
                Intent intent2 = new Intent( UpdatePetrolStationPrice.this, UpdatePetrolStationData.class);
                startActivity(intent2);
                finish();
                break;

            case R.id.menu_update_petrol_station_email:
                Intent intent3 = new Intent(UpdatePetrolStationPrice.this, UpdatePetrolStationEmail.class);
                startActivity(intent3);
                finish();
                break;

            case R.id.menu_change_update_petrol_station_password:
                Intent intent4 = new Intent(UpdatePetrolStationPrice.this, UpdatePetrolStationPassword.class);
                startActivity(intent4);
                finish();
                break;

            case R.id.menu_delete_petrol_station:
                Intent intent5 = new Intent(UpdatePetrolStationPrice.this, DeletePetrolStation.class);
                startActivity(intent5);
                finish();
                break;

            case R.id.menu_logout:
                authProfile.signOut();
                Toast.makeText(UpdatePetrolStationPrice.this, "Logged Out", Toast.LENGTH_LONG).show();
                Intent intent6 = new Intent(UpdatePetrolStationPrice.this, Login.class);
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