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
import com.example.mtailor.pojo.Pant;
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

public class PantMeasurementActivity extends AppCompatActivity {

    private FirebaseDatabase myDB;
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    Customer oldCustomer;
    Emp oldEmp;
    Pant pant, fetchedPant;
    EditText editHeight, editWaist, editSeat,editBottom, editThigh, editUttaar, editPantNotes;
    RadioGroup radioGroupPantPlates;
    RadioButton selectedPantPlates;
    Spinner pocketSpinner;

    String strPantPocket, UID, origin, id, titleText;
    boolean isEmp, isCustomer;

//    Pocket: Side, Cross

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pant_measurement);

        getSupportActionBar().setTitle("Pant Measurements");

        origin = getIntent().getStringExtra("origin");

        isEmp = origin.equals("empMeasurement");
        isCustomer = origin.equals("customerMeasurement");

        if (isEmp){oldEmp = getIntent().getExtras().getParcelable("oldEmp");}
        if (isCustomer) {oldCustomer = getIntent().getExtras().getParcelable("oldCustomer");}

        init();
        getPreviousPant();
    }

    private void init() {

        if (isCustomer){
            id = oldCustomer.getCustomerID();
            titleText = oldCustomer.getCustomerName() + "\n" + oldCustomer.getCustomerMobile();
        }
        if (isEmp){
            id = oldEmp.getEmpID();
            titleText = oldEmp.getEmpName();
        }

        //init firebase auth
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        UID = currentUser.getUid();

        //init database and references
        myDB = FirebaseDatabase.getInstance();
        rootRef = myDB.getReference().child("Users").child(UID).child("Measurements").child(id).child("Pant");

//        TextView for name of customer
        TextView cName = findViewById(R.id.customer_name_pant_measurement);
        cName.setText(titleText);


//        edit texts
        editHeight = findViewById(R.id.edit_pant_height);
        editWaist = findViewById(R.id.edit_pant_waist);
        editSeat = findViewById(R.id.edit_pant_seat);
        editBottom = findViewById(R.id.edit_pant_bottom);
        editThigh = findViewById(R.id.edit_pant_thigh);
        editUttaar = findViewById(R.id.edit_pant_uttaar);
        editPantNotes = findViewById(R.id.edit_pant_notes);

//        spinner
        pocketSpinner = findViewById(R.id.pant_pocket_spinner);

//        radio
        radioGroupPantPlates = findViewById(R.id.radiogroup_pant_plates);

//        get position of item selected in spinner
        pocketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strPantPocket = String.valueOf(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void getPreviousPant() {
//        Fetch data if already exists
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    fetchedPant = dataSnapshot.getValue(Pant.class);
//                    Set texts to text views and other fields
                    editHeight.setText(fetchedPant.getHeight());
                    editSeat.setText(fetchedPant.getSeat());
                    editWaist.setText(fetchedPant.getWaist());
                    editBottom.setText(fetchedPant.getBottom());
                    editThigh.setText(fetchedPant.getThigh());
                    editUttaar.setText(fetchedPant.getUttaar());
                    editPantNotes.setText(fetchedPant.getNotes());
//                    set radio
                    if (fetchedPant.getPlates().equals("Yes")){
                        radioGroupPantPlates.check(R.id.radio_plate_yes);
                    }
                    if (fetchedPant.getPlates().equals("No")){
                        radioGroupPantPlates.check(R.id.radio_plate_no);
                    }
//                    drop downs
                    pocketSpinner.setSelection(Integer.parseInt(fetchedPant.getPocket()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PantMeasurementActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClickPantMeasurement(View view){
        if (view.getId() == R.id.btn_save_pant_measurement){
            createPant();
        } else finish();
    }

    private void createPant() {
        String strHeight = editHeight.getText().toString().trim();
        String strWaist = editWaist.getText().toString().trim();
        String strSeat = editSeat.getText().toString().trim();
        String strBottom = editBottom.getText().toString().trim();
        String strThigh = editThigh.getText().toString().trim();
        String strUttaar = editUttaar.getText().toString().trim();
        String strNotes = editPantNotes.getText().toString().trim();

        int selectedID = radioGroupPantPlates.getCheckedRadioButtonId();
        selectedPantPlates = findViewById(selectedID);

        String strPantPlates = selectedPantPlates.getText().toString().trim();

        pant = new Pant(id, strHeight, strWaist, strSeat, strBottom, strThigh, strUttaar, strPantPocket, strPantPlates, strNotes);

        rootRef.setValue(pant).addOnCompleteListener(new OnCompleteListener<Void>() {
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
