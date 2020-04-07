package com.example.mtailor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
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

    private String UID;

    private EditText editOrgName, editOrgOwner, editMobile, editAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_organization);

        initialize();
    }

    private void registerNewOrg(){
        String orgID, orgName, orgOwner, mobile,address;
        orgName = editOrgName.getText().toString().trim();
        orgOwner = editOrgOwner.getText().toString().trim();
        mobile = editMobile.getText().toString().trim();
        address = editAddress.getText().toString().trim();

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

    public void onClickNewOrg(View view){
        switch (view.getId()){
            case R.id.btn_register_org:
//                showSnackbar("Clicked on Register");
                registerNewOrg();
                break;
            case R.id.btn_cancel_org:
//                showSnackbar("Clicked on Cancel");
                finish();
                break;
        }
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
        editAddress = findViewById(R.id.edit_org_name);
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
