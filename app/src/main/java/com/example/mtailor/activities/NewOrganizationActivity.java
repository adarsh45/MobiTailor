package com.example.mtailor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.mtailor.R;
import com.example.mtailor.pojo.Org;
import com.example.mtailor.utils.ResultDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewOrganizationActivity extends AppCompatActivity {

    private FirebaseDatabase myDB;
    private DatabaseReference rootRef, orgRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String UID, origin;
    private String orgID, orgName, orgOwner, mobile,address, strClothColor, embroidery, notes;

    private Org oldOrg;

    private EditText editOrgName, editOrgOwner, editMobile, editAddress, editEmbroidery, editNotes;
    private Spinner clothColorSpinner;
    private Button regBtn, cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_organization);

        initialize();
        getOrigin();
    }

    private void initialize() {
//        adding back button on toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init firebase auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        UID = currentUser.getUid();

        //init database and references
        myDB = FirebaseDatabase.getInstance();
        rootRef = myDB.getReference().child("Users").child(UID);

        orgRef = rootRef.child("Organizations");

//        init views
        editOrgName = findViewById(R.id.edit_org_name);
        editOrgOwner = findViewById(R.id.edit_org_owner);
        editMobile = findViewById(R.id.edit_mobile);
        editAddress = findViewById(R.id.edit_org_address);
        editEmbroidery = findViewById(R.id.edit_embroidery);
        editNotes = findViewById(R.id.edit_org_notes);
        regBtn = findViewById(R.id.btn_register_org); // reg btn
        cancelBtn = findViewById(R.id.btn_cancel_org); // cancel btn

        clothColorSpinner = findViewById(R.id.cloth_colors_spinner); // spinner for cloth-colors
        clothColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strClothColor = String.valueOf(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getOrigin() {
        origin = getIntent().getStringExtra("origin");

        switch (origin){
            case "newOrg":
                registerNewOrg();
                break;
            case "updateOrg":
                updateOrg();
                break;
        }

    }

    private void registerNewOrg(){

//        onclick listener for regBtn
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                get strings from all edit texts
                orgName = editOrgName.getText().toString().trim();
                orgOwner = editOrgOwner.getText().toString().trim();
                mobile = editMobile.getText().toString().trim();
                address = editAddress.getText().toString().trim();
                embroidery = editEmbroidery.getText().toString().trim();
                notes = editNotes.getText().toString().trim();

//                check if any of edit texts are empty
                if (orgName.isEmpty()){
                    editOrgName.setError("Please enter organization name");
                    return;
                }
                if (orgOwner.isEmpty()){
                    editOrgOwner.setError("Please enter owner name");
                    return;
                }
                if (mobile.isEmpty()){
                    editMobile.setError("Please enter mobile number");
                    return;
                }
                if (address.isEmpty()){
                    editOrgName.setError("Please enter address");
                    return;
                }

                orgID = orgRef.push().getKey();

                Org org = new Org(orgID,orgName,orgOwner,mobile,address, strClothColor, embroidery, notes);

                orgRef.child(orgID).setValue(org).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ResultDialog dialog = new ResultDialog(NewOrganizationActivity.this, task.isSuccessful());
                        dialog.show(getSupportFragmentManager(),"Result");
                    }
                });
            }
        });


    }

    private void updateOrg() {
        Bundle bundle = getIntent().getExtras();
        oldOrg = bundle.getParcelable("oldOrg");
//        change text of register btn to update
        regBtn.setText("Update");

        try {
            //        set data from oldOrg object to edit texts
            editOrgName.setText(oldOrg.getOrgName());
            editOrgOwner.setText(oldOrg.getOrgOwner());
            editMobile.setText(oldOrg.getOrgMobile());
            editAddress.setText(oldOrg.getOrgAddress());
            editEmbroidery.setText(oldOrg.getOrgEmbroidery());
            editNotes.setText(oldOrg.getOrgNotes());
//        set data to clothColor Spinner
            clothColorSpinner.setSelection(Integer.parseInt(oldOrg.getOrgClothColor()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

//        set onclick listener for reg btn
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orgID = oldOrg.getOrgID();
                orgName = editOrgName.getText().toString().trim();
                orgOwner = editOrgOwner.getText().toString().trim();
                mobile = editMobile.getText().toString().trim();
                address = editAddress.getText().toString().trim();
                embroidery = editEmbroidery.getText().toString().trim();
                notes = editNotes.getText().toString().trim();

                oldOrg.setOrgName(orgName);
                oldOrg.setOrgOwner(orgOwner);
                oldOrg.setOrgMobile(mobile);
                oldOrg.setOrgAddress(address);
                oldOrg.setOrgClothColor(strClothColor);
                oldOrg.setOrgEmbroidery(embroidery);
                oldOrg.setOrgNotes(notes);

                orgRef.child(orgID).setValue(oldOrg).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ResultDialog dialog = new ResultDialog(NewOrganizationActivity.this, task.isSuccessful());
                        dialog.show(getSupportFragmentManager(),"Result");
                    }
                });
            }
        });
    }

    //    arrow <-- for getting back to previous activity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
