package com.example.mtailor.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mtailor.R;
import com.example.mtailor.pojo.Customer;
import com.example.mtailor.utils.ResultDialog;
import com.example.mtailor.utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;
import java.util.Objects;

public class NewCustomerActivity extends AppCompatActivity {

    private FirebaseDatabase myDB;
    private DatabaseReference rootRef, customerRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String UID;
    private byte origin;
    private Bundle bundle;
    private Customer oldCustomer;

    private EditText editCustomerName, editCustomerMobile, editCustomerAddress;
    private ConstraintLayout layout;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_customer);

        initialize();
        getOrigin();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initialize() {
        layout = findViewById(R.id.new_customer_layout);
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

        //finding views from id
        editCustomerName = findViewById(R.id.edit_customer_name);
        editCustomerMobile = findViewById(R.id.edit_customer_mobile);
        editCustomerAddress = findViewById(R.id.edit_customer_address);

    }
    public void setLocaleLanguage(String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.locale = locale;
//        } else {
//            configuration.setLocale(locale);
//        }
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
//        Toast.makeText(context, "language changed", Toast.LENGTH_SHORT).show();
    }

    private void getOrigin() {
        origin = getIntent().getByteExtra("origin",Util.NEW_CUSTOMER);

//        set language according to settings
//        SharedPreferences sharedPreferences = getSharedPreferences(Util.settingsSPFileName, MODE_PRIVATE);
//        if (sharedPreferences != null){
//            String langCode = sharedPreferences.getString(Util.appLanguage, "en");
//        }
//        Util.setLocaleLanguage(this, "mr_IN");
        setLocaleLanguage("mr_rIn");

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
        Button deleteBtn = findViewById(R.id.delete_btn);
        deleteBtn.setVisibility(View.VISIBLE);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(NewCustomerActivity.this)
                .setTitle("Delete Customer")
                .setMessage("Are you sure you want to delete this customer ?")
                .setIcon(R.drawable.ic_delete)

                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customerRef.child(customerId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) finish();
                                else Util.showSnackBar(layout, "Something went wrong");
                            }
                        });
                    }
                })

                .setNegativeButton("Cancel", null)
                .show();
            }
        });

    }

    public void onClickNewCustomer(View view){
        switch (view.getId()){
            case R.id.btn_register_customer:
                //register new customer method
                if (origin == Util.UPDATE_CUSTOMER){
                    updateCustomer(oldCustomer);
                } else if (origin == Util.NEW_CUSTOMER){
                    registerNewCustomer();
                } else Util.showSnackBar(layout,"Origin not defined");
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

        assert customerID != null;
        customerRef.child(customerID).setValue(customer).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ResultDialog dialog = new ResultDialog(NewCustomerActivity.this, task.isSuccessful());
                dialog.show(getSupportFragmentManager(),"Result");
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
                ResultDialog dialog = new ResultDialog(NewCustomerActivity.this, task.isSuccessful());
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
