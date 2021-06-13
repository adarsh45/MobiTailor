package com.adarsh45.mobitailor.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.adarsh45.mobitailor.R;
import com.adarsh45.mobitailor.pojo.Customer;
import com.adarsh45.mobitailor.utils.LanguageHelper;
import com.adarsh45.mobitailor.utils.ResultDialog;
import com.adarsh45.mobitailor.utils.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class NewCustomerActivity extends AppCompatActivity {

    private static final String TAG = "NewCustomerActivity";

    private FirebaseDatabase myDB;
    private DatabaseReference rootRef, customerRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String UID;
    private byte origin;
    private Bundle bundle;
    private Customer oldCustomer;

    private EditText editCustomerName, editCustomerMobile, editCustomerAddress;
    private Button btnRegister;
    private ConstraintLayout layout;
    private LinearLayout progressLayout;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_customer);

        initialize();
        getOrigin();
//        update language of texts
        Resources resources = LanguageHelper.updateLanguage(this);
        Objects.requireNonNull(getSupportActionBar()).setTitle(resources.getString(R.string.view_customers));

    }

    private ValueEventListener offlineDeleteValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()){
                finish();
            } else {
                Util.showSnackBar(layout, "Something went wrong");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.d(TAG, "onCancelled: " + error.getMessage());
            Util.showSnackBar(layout, "Something went wrong");
        }
    };
    private ValueEventListener offlineSaveValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            btnRegister.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.GONE);
            ResultDialog dialog = new ResultDialog(NewCustomerActivity.this, snapshot.exists());
            dialog.show(getSupportFragmentManager(),"Result");
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.d(TAG, "onCancelled: " + error.getMessage());
            btnRegister.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.GONE);
            ResultDialog dialog = new ResultDialog(NewCustomerActivity.this, false);
            dialog.show(getSupportFragmentManager(),"Result");
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initialize() {
        layout = findViewById(R.id.new_customer_layout);
        progressLayout = findViewById(R.id.progress_new_customer);
        btnRegister = findViewById(R.id.btn_register_customer);
//        adding back button on toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init database and references
        myDB = FirebaseDatabase.getInstance();
        rootRef = myDB.getReference().child("Users");

        //init firebase auth
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        UID = currentUser.getUid();
        customerRef = rootRef.child(UID).child("Customers");

//        offline sync
        rootRef.keepSynced(true);
        customerRef.keepSynced(true);

        //finding views from id
        editCustomerName = findViewById(R.id.edit_customer_name);
        editCustomerMobile = findViewById(R.id.edit_customer_mobile);
        editCustomerAddress = findViewById(R.id.edit_customer_address);

    }

    private void getOrigin() {
        origin = getIntent().getByteExtra("origin",Util.NEW_CUSTOMER);

        if (origin == Util.UPDATE_CUSTOMER) {
            bundle = getIntent().getExtras();
            assert bundle != null;
            oldCustomer = bundle.getParcelable("oldCustomer");
            Button registerBtn = findViewById(R.id.btn_register_customer);
            registerBtn.setText(R.string.update);
            editCustomerName.setText(oldCustomer.getCustomerName());
            editCustomerMobile.setText(oldCustomer.getCustomerMobile());
            editCustomerAddress.setText(oldCustomer.getCustomerAddress());
//            callTheCustomer();
            Util.callTheCustomer(NewCustomerActivity.this, R.id.edit_customer_mobile, R.id.call_btn);

            deleteCustomer(oldCustomer.getCustomerID());
        }
    }

    private void deleteCustomer(final String customerId) {
//        TODO: delete measurements associated with that customer also
        Button deleteBtn = findViewById(R.id.delete_btn);
        deleteBtn.setVisibility(View.VISIBLE);
        deleteBtn.setOnClickListener(v -> new AlertDialog.Builder(NewCustomerActivity.this)
        .setTitle("Delete Customer")
        .setMessage("Are you sure you want to delete this customer ?")
        .setIcon(R.drawable.ic_delete)

        .setPositiveButton("DELETE", (dialog, which) -> {
            customerRef.child(customerId).removeValue().addOnCompleteListener(task -> {
//                            show these popups only if activity is running
                if (!isFinishing()) {
                    if (task.isSuccessful()) finish();
                    else Util.showSnackBar(layout, "Something went wrong");
                }
            });
//                        finish activity(after delete) when device is offline (irrespective of above onComplete callback)
            if (!Util.isNetworkAvailable(NewCustomerActivity.this)){
                customerRef.addListenerForSingleValueEvent(offlineDeleteValueEventListener);
            }
        })

        .setNegativeButton("Cancel", null)
        .show());

    }

    public void onClickNewCustomer(View view){
        if (view.getId() == R.id.btn_register_customer){
            //register new customer method
            btnRegister.setVisibility(View.GONE);
            progressLayout.setVisibility(View.VISIBLE);
            if (origin == Util.UPDATE_CUSTOMER){
                updateCustomer(oldCustomer);
            } else if (origin == Util.NEW_CUSTOMER){
                registerNewCustomer();
            } else Util.showSnackBar(layout,"Origin not defined");
        } else if (view.getId() == R.id.btn_cancel_customer){
            finish();
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

        assert customerID != null;
        customerRef.child(customerID).setValue(customer).addOnCompleteListener(task -> {
//          show these popups only if activity is running
            if (!isFinishing()) {
                btnRegister.setVisibility(View.VISIBLE);
                progressLayout.setVisibility(View.GONE);
                ResultDialog dialog = new ResultDialog(NewCustomerActivity.this, task.isSuccessful());
                dialog.show(getSupportFragmentManager(), "Result");
            }
        });

//        show success msg when device is offline (irrespective of onComplete callback)
        if (!Util.isNetworkAvailable(NewCustomerActivity.this)) {
            customerRef.addListenerForSingleValueEvent(offlineSaveValueEventListener);
        }
    }

    private void updateCustomer(Customer currentCustomer){
        String customerName = editCustomerName.getText().toString().trim();
        String customerMobile = editCustomerMobile.getText().toString().trim();
        String customerAddress = editCustomerAddress.getText().toString().trim();

        currentCustomer.setCustomerName(customerName);
        currentCustomer.setCustomerMobile(customerMobile);
        currentCustomer.setCustomerAddress(customerAddress);

        String customerID = currentCustomer.getCustomerID();

        customerRef.child(customerID).setValue(currentCustomer).addOnCompleteListener(task -> {
//          show these popups only if activity is running
            if (!isFinishing()) {
                btnRegister.setVisibility(View.VISIBLE);
                progressLayout.setVisibility(View.GONE);
                ResultDialog dialog = new ResultDialog(NewCustomerActivity.this, task.isSuccessful());
                dialog.show(getSupportFragmentManager(), "Result");
            }
        });

        if(!Util.isNetworkAvailable(NewCustomerActivity.this)){
            customerRef.addListenerForSingleValueEvent(offlineSaveValueEventListener);
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
