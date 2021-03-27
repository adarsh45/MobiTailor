package com.adarsh45.mobitailor.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adarsh45.mobitailor.R;
import com.adarsh45.mobitailor.pojo.Customer;
import com.adarsh45.mobitailor.pojo.Emp;
import com.adarsh45.mobitailor.pojo.Shirt;
import com.adarsh45.mobitailor.utils.LanguageHelper;
import com.adarsh45.mobitailor.utils.ResultDialog;
import com.adarsh45.mobitailor.utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ShirtMeasurementActivity extends AppCompatActivity {

    private static final String TAG = "ShirtMeasurement";

    private FirebaseDatabase myDB;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    Customer oldCustomer;
    Emp oldEmp;
    Shirt shirt, fetchedShirt;
    TextView textLastUpdateDate;
    EditText shirtHeight, shirtChest, shirtStomach, shirtSeat, shirtShoulder,
            shirtSleeveFull, shirtSleeveFullCuff, shirtSleeveFullBicep,
            shirtSleeveHalf, shirtSleeveHalfBicep, shirtCollar, shirtFrontChest,
            shirtFrontStomach, shirtFrontSeat, shirtNotes;
    RadioGroup shirtRadioGroupPatti, shirtRadioGroupSilai;
    RadioButton radioBtnBoxPatti, radioBtnInPatti, radioBtnCoverSilai, radioBtnPlainSilai;
    Spinner typeSpinner;

    String strShirtType, UID, id, titleText;
    byte origin;
    boolean isEmp, isCustomer;

    private Resources resources;

//    String[] type = getResources().getStringArray(R.array.type);
//    Type: Apple, Open, Manilla, 3 Button Shirt, Safari

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shirt_measurement);

        origin = getIntent().getByteExtra("origin", Util.CUSTOMER_MEASUREMENT);

        isEmp = origin == Util.EMP_MEASUREMENT;
        isCustomer = origin == Util.CUSTOMER_MEASUREMENT;

        if (isEmp){oldEmp = Objects.requireNonNull(getIntent().getExtras()).getParcelable("oldEmp");}
        if (isCustomer) {oldCustomer = Objects.requireNonNull(getIntent().getExtras()).getParcelable("oldCustomer");}

        initialize();


        resources = LanguageHelper.updateLanguage(this);
        Objects.requireNonNull(getSupportActionBar()).setTitle(resources.getString(R.string.shirt_measurements));

        getPreviousShirt();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initialize() {
//        adding back button on toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        textLastUpdateDate = findViewById(R.id.textLastUpdateDateShirt);

//        editTexts
        shirtHeight = findViewById(R.id.shirt_height);
        shirtChest = findViewById(R.id.shirt_chest);
        shirtStomach = findViewById(R.id.shirt_stomach);
        shirtSeat = findViewById(R.id.shirt_seat);
        shirtShoulder = findViewById(R.id.shirt_shoulder);
        shirtSleeveFull = findViewById(R.id.shirt_sleeve_full);
        shirtSleeveFullCuff = findViewById(R.id.shirt_sleeve_full_cuff);
        shirtSleeveFullBicep = findViewById(R.id.shirt_sleeve_full_bicep);
        shirtSleeveHalf = findViewById(R.id.shirt_sleeve_half);
        shirtSleeveHalfBicep = findViewById(R.id.shirt_sleeve_half_bicep);

        shirtCollar = findViewById(R.id.shirt_collar);
        shirtFrontChest = findViewById(R.id.shirt_front_chest);
        shirtFrontStomach = findViewById(R.id.shirt_front_stomach);
        shirtFrontSeat = findViewById(R.id.shirt_front_seat);

        shirtNotes = findViewById(R.id.shirt_notes);

//        spinner
        typeSpinner = findViewById(R.id.shirt_type_spinner);

//        RadioGroup
        shirtRadioGroupPatti = findViewById(R.id.radio_group_shirt_patti);
        shirtRadioGroupSilai = findViewById(R.id.radio_group_shirt_silai);

//        radioButtons
        radioBtnBoxPatti = findViewById(R.id.radio_box_patti);
        radioBtnInPatti = findViewById(R.id.radio_in_patti);
        radioBtnCoverSilai = findViewById(R.id.radio_cover_silai);
        radioBtnPlainSilai = findViewById(R.id.radio_plain_silai);


        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strShirtType = String.valueOf(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(ShirtMeasurementActivity.this, "Nothing selected!", Toast.LENGTH_SHORT).show();
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
                    try {
                        shirtHeight.setText(fetchedShirt.getShirtHeightValue());
                        shirtChest.setText(fetchedShirt.getShirtChestValue());
                        shirtStomach.setText(fetchedShirt.getShirtStomachValue());
                        shirtSeat.setText(fetchedShirt.getShirtSeatValue());
                        shirtShoulder.setText(fetchedShirt.getShirtShoulderValue());
                        shirtSleeveFull.setText(fetchedShirt.getShirtSleeveFullValue());
                        shirtSleeveFullCuff.setText(fetchedShirt.getShirtSleeveFullCuffValue());
                        shirtSleeveFullBicep.setText(fetchedShirt.getShirtSleeveFullBicepValue());
                        shirtSleeveHalf.setText(fetchedShirt.getShirtSleeveHalfValue());
                        shirtSleeveHalfBicep.setText(fetchedShirt.getShirtSleeveHalfBicepValue());
                        shirtCollar.setText(fetchedShirt.getShirtCollarValue());
                        shirtFrontChest.setText(fetchedShirt.getShirtFrontChestValue());
                        shirtFrontStomach.setText(fetchedShirt.getShirtFrontStomachValue());
                        shirtFrontSeat.setText(fetchedShirt.getShirtFrontSeatValue());
                        shirtNotes.setText(fetchedShirt.getNotes());

                        textLastUpdateDate.setText(fetchedShirt.getLastUpdateDate());

//                    set radio
//                        Util.checkRadio(fetchedShirt.getShirtPatti(), resources.getString(R.string.shirt_box_patti), radioBtnBoxPatti);
//                        Util.checkRadio(fetchedShirt.getShirtPatti(), resources.getString(R.string.shirt_in_patti), radioBtnInPatti);
//                        Util.checkRadio(fetchedShirt.getShirtSilai(), resources.getString(R.string.shirt_plain_silai), radioBtnPlainSilai);
//                        Util.checkRadio(fetchedShirt.getShirtSilai(), resources.getString(R.string.shirt_cover_silai), radioBtnCoverSilai);

                        Util.checkRadio(ShirtMeasurementActivity.this, shirtRadioGroupPatti, fetchedShirt.getShirtPatti());
                        Util.checkRadio(ShirtMeasurementActivity.this, shirtRadioGroupSilai, fetchedShirt.getShirtSilai());

//                    drop downs
                        typeSpinner.setSelection(Integer.parseInt(fetchedShirt.getShirtType()));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
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
//            save btn clicked
            createShirt();
//            cancel btn clicked
        } else finish();
    }

    private void createShirt() {
        String shirtHeightValue, shirtChestValue, shirtStomachValue, shirtSeatValue, shirtShoulderValue,
              shirtSleeveFullValue, shirtSleeveFullCuffValue, shirtSleeveFullBicepValue, shirtSleeveHalfValue,
              shirtSleeveHalfBicepValue, shirtCollarValue, shirtFrontChestValue, shirtFrontStomachValue,
              shirtFrontSeatValue;
        String strShirtNotes;

        String lastUpdateDate = Util.getCurrentDate();

        shirtHeightValue = shirtHeight.getText().toString().trim();
        shirtChestValue = shirtChest.getText().toString().trim();
        shirtStomachValue = shirtStomach.getText().toString().trim();
        shirtSeatValue = shirtSeat.getText().toString().trim();
        shirtShoulderValue = shirtShoulder.getText().toString().trim();
        shirtSleeveFullValue = shirtSleeveFull.getText().toString().trim();
        shirtSleeveFullCuffValue = shirtSleeveFullCuff.getText().toString().trim();
        shirtSleeveFullBicepValue = shirtSleeveFullBicep.getText().toString().trim();
        shirtSleeveHalfValue = shirtSleeveHalf.getText().toString().trim();
        shirtSleeveHalfBicepValue = shirtSleeveHalfBicep.getText().toString().trim();
        shirtCollarValue = shirtCollar.getText().toString().trim();
        shirtFrontChestValue = shirtFrontChest.getText().toString().trim();
        shirtFrontStomachValue = shirtFrontStomach.getText().toString().trim();
        shirtFrontSeatValue = shirtFrontSeat.getText().toString().trim();
        strShirtNotes = shirtNotes.getText().toString().trim();

//        get text of that selected radio btn
        String strShirtPatti = Util.getTextFromRadioGroup(this, shirtRadioGroupPatti);
        String strShirtSilai = Util.getTextFromRadioGroup(this, shirtRadioGroupSilai);

//        shirt.setUID(id);
        shirt = new Shirt(id, lastUpdateDate,strShirtType,strShirtPatti,strShirtSilai, strShirtNotes, shirtHeightValue,
                shirtChestValue, shirtStomachValue, shirtSeatValue, shirtShoulderValue,
                shirtSleeveFullValue, shirtSleeveFullCuffValue, shirtSleeveFullBicepValue,
                shirtSleeveHalfValue, shirtSleeveHalfBicepValue, shirtCollarValue, shirtFrontChestValue,
                shirtFrontStomachValue, shirtFrontSeatValue);


        myRef.setValue(shirt).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ResultDialog dialog = new ResultDialog(ShirtMeasurementActivity.this, task.isSuccessful());
                dialog.show(getSupportFragmentManager(),"Result");
            }
        });

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
