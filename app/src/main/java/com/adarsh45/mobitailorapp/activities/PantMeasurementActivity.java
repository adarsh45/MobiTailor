package com.adarsh45.mobitailorapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adarsh45.mobitailorapp.R;
import com.adarsh45.mobitailorapp.pojo.Customer;
import com.adarsh45.mobitailorapp.pojo.Emp;
import com.adarsh45.mobitailorapp.pojo.Pant;
import com.adarsh45.mobitailorapp.utils.LanguageHelper;
import com.adarsh45.mobitailorapp.utils.ResultDialog;
import com.adarsh45.mobitailorapp.utils.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class PantMeasurementActivity extends AppCompatActivity {

    private static final String TAG = "PantMeasurement";

    private FirebaseDatabase myDB;
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    Customer oldCustomer;
    Emp oldEmp;
    Pant pant, fetchedPant;
    TextView textLastUpdateDate;
    EditText editHeight, editWaist, editSeat, editBottom, editKnee, editThigh1, editThigh2, editSeatRound, editSeatUtaar, editNotes;
    RadioGroup radioGroupPantType, radioGroupPantPocket, radioGroupPantPlates,
                radioGroupPantSide, radioGroupPantStitch, radioGroupPantBackPocket;
    Spinner pantTypeSpinner;

    private Button btnSave;
    private LinearLayout progressLayout;

    String strPantType;
    String UID, id, titleText;
    private byte origin;
    boolean isEmp, isCustomer;

    private Resources resources;

//    Pocket: Side, Cross

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pant_measurement);

        origin = getIntent().getByteExtra("origin", Util.CUSTOMER_MEASUREMENT);

        isEmp = origin == Util.EMP_MEASUREMENT;
        isCustomer = origin == Util.CUSTOMER_MEASUREMENT;

        if (isEmp){oldEmp = Objects.requireNonNull(getIntent().getExtras()).getParcelable("oldEmp");}
        if (isCustomer) {oldCustomer = Objects.requireNonNull(getIntent().getExtras()).getParcelable("oldCustomer");}

        initialize();
        getPreviousPant();

        resources = LanguageHelper.updateLanguage(this);
//        updateLanguage();
        Objects.requireNonNull(getSupportActionBar()).setTitle(resources.getString(R.string.pant_measurements));
    }

    ValueEventListener offlineEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            btnSave.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.GONE);
            ResultDialog dialog = new ResultDialog(PantMeasurementActivity.this, snapshot.exists());
            dialog.show(getSupportFragmentManager(),"Result");
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            btnSave.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.GONE);
            Log.d(TAG, "onCancelled: " + error.getMessage());
            ResultDialog dialog = new ResultDialog(PantMeasurementActivity.this, false);
            dialog.show(getSupportFragmentManager(),"Result");
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initialize() {
//        adding back button on toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        assert currentUser != null;
        UID = currentUser.getUid();

        //init database and references
        myDB = FirebaseDatabase.getInstance();
        rootRef = myDB.getReference().child("Users").child(UID).child("Measurements").child(id).child("Pant");

//        TextView for name of customer
        TextView cName = findViewById(R.id.customer_name_pant_measurement);
        cName.setText(titleText);

        textLastUpdateDate = findViewById(R.id.textLastUpdateDatePant);

//        edit texts
        editHeight = findViewById(R.id.edit_pant_height);
        editWaist = findViewById(R.id.edit_pant_waist);
        editSeat = findViewById(R.id.edit_pant_seat);
        editBottom = findViewById(R.id.edit_pant_bottom);
        editKnee = findViewById(R.id.edit_pant_knee);
        editThigh1 = findViewById(R.id.edit_pant_thigh_1);
        editThigh2 = findViewById(R.id.edit_pant_thigh_2);
        editSeatRound = findViewById(R.id.edit_pant_seat_round);
        editSeatUtaar = findViewById(R.id.edit_pant_seat_utaar);
        editNotes = findViewById(R.id.edit_pant_notes);

//        radio groups
//        radioGroupPantType = findViewById(R.id.radiogroup_pant_type);
        radioGroupPantPocket = findViewById(R.id.radiogroup_pant_pocket);
        radioGroupPantPlates = findViewById(R.id.radiogroup_pant_plates);
        radioGroupPantSide = findViewById(R.id.radiogroup_pant_side);
        radioGroupPantStitch = findViewById(R.id.radiogroup_pant_stitch);
        radioGroupPantBackPocket = findViewById(R.id.radiogroup_pant_back_pocket);
//        spinner
        pantTypeSpinner = findViewById(R.id.pant_type_spinner);

        btnSave = findViewById(R.id.btn_save_pant_measurement);
        progressLayout = findViewById(R.id.progress_pant_measurement);

//        get text from spinner
        pantTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strPantType = String.valueOf(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(PantMeasurementActivity.this, "Select something fot Pant type!", Toast.LENGTH_SHORT).show();
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
                   try {
                       assert fetchedPant != null;
                       editHeight.setText(fetchedPant.getHeight());
                       editWaist.setText(fetchedPant.getWaist());
                       editSeat.setText(fetchedPant.getSeat());
                       editBottom.setText(fetchedPant.getBottom());
                       editKnee.setText(fetchedPant.getKnee());
                       editThigh1.setText(fetchedPant.getThigh1());
                       editThigh2.setText(fetchedPant.getThigh2());
                       editSeatRound.setText(fetchedPant.getSeatRound());
                       editSeatUtaar.setText(fetchedPant.getSeatUtaar());
                       editNotes.setText(fetchedPant.getNotes());

                       textLastUpdateDate.setText(fetchedPant.getLastUpdateDate());

//                    set radio
//                    Util.checkRadio(radioGroupPantType, fetchedPant.getPantType());
                       Util.checkRadio(PantMeasurementActivity.this, radioGroupPantPocket, fetchedPant.getPantPocket());
                       Util.checkRadio(PantMeasurementActivity.this, radioGroupPantPlates, fetchedPant.getPantPlates());
                       Util.checkRadio(PantMeasurementActivity.this, radioGroupPantSide, fetchedPant.getPantSide());
                       Util.checkRadio(PantMeasurementActivity.this, radioGroupPantStitch, fetchedPant.getPantStitch());
                       Util.checkRadio(PantMeasurementActivity.this, radioGroupPantBackPocket, fetchedPant.getPantBackPocket());
//                    set spinner
                       pantTypeSpinner.setSelection(Integer.parseInt(fetchedPant.getPantType()));
                   } catch (NumberFormatException e) {
                       e.printStackTrace();
                   }

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
            btnSave.setVisibility(View.GONE);
            progressLayout.setVisibility(View.VISIBLE);
            createPant();
        } else finish();
    }

    private void createPant() {
//        get text from edit texts
        String strHeight = editHeight.getText().toString().trim();
        String strWaist = editWaist.getText().toString().trim();
        String strSeat = editSeat.getText().toString().trim();
        String strBottom = editBottom.getText().toString().trim();
        String strKnee = editKnee.getText().toString().trim();
        String strThigh1 = editThigh1.getText().toString().trim();
        String strThigh2 = editThigh2.getText().toString().trim();
        String strSeatRound = editSeatRound.getText().toString().trim();
        String strUtaar = editSeatUtaar.getText().toString().trim();
        String strNotes = editNotes.getText().toString().trim();

//        get current date
        String lastUpdateDate = Util.getCurrentDate();

//        get text from radio groups
//        String strPantType = Util.getTextFromRadioGroup(this, radioGroupPantType);
        String strPantPocket = Util.getTextFromRadioGroup(this, radioGroupPantPocket);
        String strPantPlates = Util.getTextFromRadioGroup(this, radioGroupPantPlates);
        String strPantSide = Util.getTextFromRadioGroup(this, radioGroupPantSide);
        String strPantStitch = Util.getTextFromRadioGroup(this, radioGroupPantStitch);
        String strPantBackPocket = Util.getTextFromRadioGroup(this, radioGroupPantBackPocket);


        pant = new Pant(id,lastUpdateDate,strHeight, strWaist, strSeat, strBottom, strKnee, strThigh1, strThigh2, strSeatRound, strUtaar, strNotes,
                strPantType, strPantPocket, strPantPlates, strPantSide, strPantStitch, strPantBackPocket);

        rootRef.setValue(pant).addOnCompleteListener(task -> {
//            show these popups only if activity is running
            if (!isFinishing()) {
                btnSave.setVisibility(View.VISIBLE);
                progressLayout.setVisibility(View.GONE);
                ResultDialog dialog = new ResultDialog(PantMeasurementActivity.this, task.isSuccessful());
                dialog.show(getSupportFragmentManager(), "Result");
            }
        });

        //        show success when offline
        if (!Util.isNetworkAvailable(PantMeasurementActivity.this)){
            rootRef.addListenerForSingleValueEvent(offlineEventListener);
        }
    }

    //    for getting back to previous activity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
