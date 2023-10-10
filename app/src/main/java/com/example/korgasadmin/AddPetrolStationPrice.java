package com.example.korgasadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AddPetrolStationPrice extends AppCompatActivity {

    private EditText editTextGasolinePrice, editTextDieselPrice, editTextKerosenePrice;
    private ProgressBar progressBar;

    private final  static  int REQUEST_CODE = 100;

    //    disable physical back button
    @Override
    public void onBackPressed() {
        // app icon in action bar clicked; go home
        Intent intent = new Intent(this, AddPetrolStationData.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_petrol_station_price);

        getSupportActionBar().setTitle("Add Price");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        progressBar = findViewById(R.id.idProgressBar);

        editTextGasolinePrice = findViewById(R.id.idTIETGasolinePrice);
        editTextDieselPrice = findViewById(R.id.idTIETDieselPrice);
        editTextKerosenePrice = findViewById(R.id.idTIETKerosenePrice);

        MaterialButton materialButtonAdd = findViewById(R.id.idBtnAdd);

        materialButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                obtain data enter by user
                String textGasolinePrice = editTextGasolinePrice.getText().toString().trim();
                String textDieselPrice = editTextDieselPrice.getText().toString().trim();
                String textKerosenePrice = editTextKerosenePrice.getText().toString().trim();

                //  Gasoline Price validation
                if (textGasolinePrice.isEmpty()){
                    editTextGasolinePrice.setError("Gasoline Price?");
                    editTextGasolinePrice.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                else if (!textGasolinePrice.contains(".")) {
                    editTextGasolinePrice.setError("Please Input a Decimal point(.) like 99.99");
                    editTextGasolinePrice.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                //  diesel price validation
                else if (textDieselPrice.isEmpty()){
                    editTextDieselPrice.setError("Diesel Price?");
                    editTextDieselPrice.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                else if (!textDieselPrice.contains(".")) {
                    editTextDieselPrice.setError("Please Input a Decimal point(.) like 99.99");
                    editTextDieselPrice.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                //  kerosene Price validation
                else if (textKerosenePrice.isEmpty()){
                    editTextKerosenePrice.setError("Kerosene Price?");
                    editTextKerosenePrice.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                else if (!textKerosenePrice.contains(".")) {
                    editTextKerosenePrice.setError("Please Input a Decimal point(.) like 99.99");
                    editTextKerosenePrice.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                else {

                    progressBar.setVisibility(View.VISIBLE);

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Petrol Stations Registered").child(firebaseUser.getUid());

//                  below this code is like finding reference but we added
                    databaseReference.child("gasolinePrice").setValue(textGasolinePrice);
                    databaseReference.child("dieselPrice").setValue(textDieselPrice);
                    databaseReference.child("kerosenePrice").setValue(textKerosenePrice);

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Toast.makeText(AddPetrolStationPrice.this, "Price added", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddPetrolStationPrice.this, AddPetrolStationLocation.class);
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