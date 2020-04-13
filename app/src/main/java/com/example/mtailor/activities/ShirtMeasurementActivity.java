package com.example.mtailor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mtailor.R;
import com.example.mtailor.pojo.Customer;
import com.example.mtailor.pojo.Emp;
import com.example.mtailor.pojo.Shirt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShirtMeasurementActivity extends AppCompatActivity {

    private FirebaseDatabase myDB;
    private DatabaseReference rootRef, myRef, empRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    Customer oldCustomer;
    Emp oldEmp;
    Shirt shirt, fetchedShirt;
    EditText shirtHeight, shirtChest, shirtFront, shirtShoulder, shirtSleeves, shirtCollar, shirtPocket, shirtNotes;
    RadioGroup shirtRadioSleeves;
    RadioButton radioSleeveHalf, radioSleeveFull, radioSleeveBoth ,selectedRadioSleeve;
    Spinner typeSpinner;

    String strShirtType, UID, origin, id, titleText;
    boolean isEmp, isCustomer;

//    String[] type = getResources().getStringArray(R.array.type);
//    Type: Apple, Open, Manilla, 3 Button Shirt, Safari

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shirt_measurement);

        getSupportActionBar().setTitle("Shirt Measurements");

        origin = getIntent().getStringExtra("origin");

        isEmp = origin.equals("empMeasurement");
        isCustomer = origin.equals("customerMeasurement");

        if (isEmp){oldEmp = getIntent().getExtras().getParcelable("oldEmp");}
        if (isCustomer) {oldCustomer = getIntent().getExtras().getParcelable("oldCustomer");}

        init();
        getPreviousShirt();

    }

    private void init() {

        if (isCustomer) {
            id = oldCustomer.getCustomerID();
            titleText = oldCustomer.getCustomerName() + "\n" + oldCustomer.getCustomerMobile();
        }
        if (isEmp) {
            id = oldEmp.getEmpID();
            titleText = oldEmp.getEmpName();
        }

        //init firebase auth
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        UID = currentUser.getUid();

        //init database and references
        myDB = FirebaseDatabase.getInstance();

        myRef = myDB.getReference().child("Users").child(UID).child("Measurements").child(id).child("Shirt");


//        TextView for name of customer
        TextView cName = findViewById(R.id.customer_name_shirt_measurement);
        cName.setText(titleText);

//        editTexts
        shirtHeight = findViewById(R.id.shirt_height);
        shirtChest = findViewById(R.id.shirt_chest);
        shirtFront = findViewById(R.id.shirt_front);
        shirtShoulder = findViewById(R.id.shirt_shoulder);
        shirtSleeves = findViewById(R.id.shirt_sleeves);
        shirtCollar = findViewById(R.id.shirt_collar);
        shirtPocket = findViewById(R.id.shirt_pocket);
        shirtNotes = findViewById(R.id.shirt_notes);

//        spinner
        typeSpinner = findViewById(R.id.shirt_type_spinner);

//        RadioGroup
        shirtRadioSleeves = findViewById(R.id.radiogroup_shirt_sleeves);
//        radioButtons
        radioSleeveHalf = findViewById(R.id.radio_sleeve_half);
        radioSleeveFull = findViewById(R.id.radio_sleeve_full);
        radioSleeveBoth = findViewById(R.id.radio_sleeve_both);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strShirtType = String.valueOf(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getPreviousShirt() {
//        Fetch data if already exists
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    fetchedShirt = dataSnapshot.getValue(Shirt.class);
//                    Set texts to textviews and other fields
                    shirtHeight.setText(fetchedShirt.getHeight());
                    shirtChest.setText(fetchedShirt.getChest());
                    shirtFront.setText(fetchedShirt.getFront());
                    shirtShoulder.setText(fetchedShirt.getShoulder());
                    shirtSleeves.setText(fetchedShirt.getSleevesText());
                    shirtCollar.setText(fetchedShirt.getCollar());
                    shirtPocket.setText(fetchedShirt.getPocket());
                    shirtNotes.setText(fetchedShirt.getNotes());
//                    set radio
                    if (fetchedShirt.getSleeves().equals("Half")){
                        shirtRadioSleeves.check(R.id.radio_sleeve_half);
                    }
                    if (fetchedShirt.getSleeves().equals("Full")){
                        shirtRadioSleeves.check(R.id.radio_sleeve_full);
                    }
                    if (fetchedShirt.getSleeves().equals("Half")){
                        shirtRadioSleeves.check(R.id.radio_sleeve_both);
                    }
//                    drop downs
                    typeSpinner.setSelection(Integer.parseInt(fetchedShirt.getType()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ShirtMeasurementActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClickShirtMeasurement(View view){
        if (view.getId() == R.id.btn_save_shirt_measurement){
//            Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
            createShirt();

        } else finish();
    }

    private void createShirt() {
        String strShirtHeight = shirtHeight.getText().toString().trim();
        String strShirtChest = shirtChest.getText().toString().trim();
        String strShirtFront = shirtFront.getText().toString().trim();
        String strShirtShoulder = shirtShoulder.getText().toString().trim();
        String strShirtSleeves = shirtSleeves.getText().toString().trim();
        String strShirtCollar = shirtCollar.getText().toString().trim();
        String strShirtPocket = shirtPocket.getText().toString().trim();
        String strShirtNotes = shirtNotes.getText().toString().trim();

        int selectedID = shirtRadioSleeves.getCheckedRadioButtonId();
        selectedRadioSleeve = findViewById(selectedID);

        String strShirtSleeveRadio = selectedRadioSleeve.getText().toString().trim();


        shirt = new Shirt(id, strShirtHeight, strShirtChest, strShirtFront,strShirtShoulder,strShirtSleeveRadio, strShirtSleeves, strShirtCollar, strShirtPocket, strShirtType, strShirtNotes);

        myRef.setValue(shirt).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    showSnackbar("Success!");
                } else showSnackbar("Failure!");
            }
        });

    }

    public void showSnackbar(CharSequence text){
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.pant_measurement_layout),text,Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }



}
