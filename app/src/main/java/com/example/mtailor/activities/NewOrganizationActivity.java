package com.example.mtailor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mtailor.R;
import com.example.mtailor.pojo.Org;
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
    private String orgID, orgName, orgOwner, mobile,address;

    private Org oldOrg;

    private EditText editOrgName, editOrgOwner, editMobile, editAddress;
    private Button regBtn, cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_organization);

        initialize();
        getOrigin();
    }

    private void initialize() {

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
        regBtn = findViewById(R.id.btn_register_org); // reg btn
        cancelBtn = findViewById(R.id.btn_cancel_org); // cancel btn

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

                Org org = new Org(orgID,orgName,orgOwner,mobile,address);

                orgRef.child(orgID).setValue(org).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            showSnackbar("Successful");
                        } else showSnackbar("Failure");
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
//        set data from oldOrg object to edit texts
        editOrgName.setText(oldOrg.getOrgName());
        editOrgOwner.setText(oldOrg.getOrgOwner());
        editMobile.setText(oldOrg.getOrgMobile());
        editAddress.setText(oldOrg.getOrgAddress());

//        set onclick listener for reg btn
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orgID = oldOrg.getOrgID();
                orgName = editOrgName.getText().toString().trim();
                orgOwner = editOrgOwner.getText().toString().trim();
                mobile = editMobile.getText().toString().trim();
                address = editAddress.getText().toString().trim();

                oldOrg.setOrgName(orgName);
                oldOrg.setOrgOwner(orgOwner);
                oldOrg.setOrgMobile(mobile);
                oldOrg.setOrgAddress(address);

                orgRef.child(orgID).setValue(oldOrg).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            showSnackbar("Updated successfully!");
                        } else showSnackbar("Something went wrong! Please try again...");
                    }
                });
            }
        });
    }

    public void showSnackbar(CharSequence text){
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.new_org_layout),text,Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

}
