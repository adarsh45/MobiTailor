package com.example.mtailor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mtailor.R;
import com.example.mtailor.pojo.Customer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewCustomerActivity extends AppCompatActivity {

    private FirebaseDatabase myDB;
    private DatabaseReference rootRef, customerRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String UID;
    private String origin;
    private Bundle bundle;
    private Customer oldCustomer;

    private EditText editCustomerName, editCustomerMobile, editCustomerAddress;

    private Dialog popupDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_customer);

        initialize();
        getOrigin();
    }

    private void initialize() {
        //init database and references
        myDB = FirebaseDatabase.getInstance();
        rootRef = myDB.getReference().child("Users");

        //init firebase auth
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        UID = currentUser.getUid();
        customerRef = rootRef.child(UID).child("Customers");

        //finding views from id
        editCustomerName = findViewById(R.id.edit_customer_name);
        editCustomerMobile = findViewById(R.id.edit_customer_mobile);
        editCustomerAddress = findViewById(R.id.edit_customer_address);

    }

    private void getOrigin(){
        origin = getIntent().getStringExtra("origin");
        bundle = getIntent().getExtras();
        oldCustomer = bundle.getParcelable("oldCustomer");

        if(origin.equals("updateCustomer")){
            Button registerBtn = findViewById(R.id.btn_register_customer);
            registerBtn.setText("Update");
            editCustomerName.setText(oldCustomer.getCustomerName());
            editCustomerMobile.setText(oldCustomer.getCustomerMobile());
            editCustomerAddress.setText(oldCustomer.getCustomerAddress());
        }
    }

    public void showSnackbar(CharSequence text){
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.new_customer_layout),text,Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private void createPopupDialog(boolean result){
        //setting up popup dialog
        popupDialog = new Dialog(this);

        if (result){
            //creating success popup dialog
            popupDialog.setContentView(R.layout.success_dialog);
            Button okBtn = popupDialog.findViewById(R.id.success_ok_btn);
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupDialog.dismiss();
                    finish();
                }
            });

            popupDialog.show();

        } else {
            //creating failure popup dialog
            popupDialog.setContentView(R.layout.failure_dialog);
            Button okBtn = popupDialog.findViewById(R.id.failure_ok_btn);
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupDialog.dismiss();
                    finish();
                }
            });

            popupDialog.show();
        }
    }

    public void onClickNewCustomer(View view){
        switch (view.getId()){
            case R.id.btn_register_customer:
                //register new customer method
                if (origin.equals("updateCustomer")){
                    updateCustomer(oldCustomer);
                } else if (origin.equals("newCustomer")){
                    registerNewCustomer();
                } else showSnackbar("Origin not defined");
                break;
            case R.id.btn_cancel_customer:
                finish();
                break;
        }
    }

    private void registerNewCustomer() {
        String customerName = editCustomerName.getText().toString().trim();
        String customerMobile = editCustomerMobile.getText().toString().trim();
        String customerAddress = editCustomerAddress.getText().toString().trim();

        if (customerName.isEmpty()){
            editCustomerName.setError("Please enter name!");
            return;
        }
        if (customerMobile.isEmpty()){
            editCustomerMobile.setError("Please enter mobile!");
            return;
        }
        if (customerAddress.isEmpty()){
            editCustomerAddress.setError("Please enter address!");
            return;
        }

        String customerID = customerRef.push().getKey();

        Customer customer = new Customer(customerID, customerName, customerMobile, customerAddress);

        customerRef.child(customerID).setValue(customer).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
//                    showSnackbar("Added Successfully!");
                    createPopupDialog(true);

                } else createPopupDialog(false);
            }
        });
    }

    private void updateCustomer(Customer currentCustomer){
        String customerName = editCustomerName.getText().toString().trim();
        String customerMobile = editCustomerMobile.getText().toString().trim();
        String customerAddress = editCustomerAddress.getText().toString().trim();

        currentCustomer.setCustomerName(customerName);
        currentCustomer.setCustomerMobile(customerMobile);
        currentCustomer.setCustomerAddress(customerAddress);

        String customerID = currentCustomer.getCustomerID();
        customerRef.child(customerID).setValue(currentCustomer).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    showSnackbar("Updated successfully!");
                }else showSnackbar("Error while updating!");
            }
        });
    }

}
