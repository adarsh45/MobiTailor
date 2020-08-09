package com.example.mtailor.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.mtailor.R;
import com.example.mtailor.pojo.Customer;
import com.example.mtailor.pojo.Emp;
import com.example.mtailor.utils.Util;

import java.util.Objects;

public class SelectProductActivity extends AppCompatActivity {

    Customer oldCustomer;
    Emp oldEmp;
    byte origin;
    boolean isEmp, isCustomer;

//    origin = customerMeasurement (from ShowCustomers Activity & CustomerAdapter)
//    origin = empMeasurement (from ShowCustomers Activity & EmpAdapter )
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Select Item");
//        adding back button on toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        origin = getIntent().getByteExtra("origin", Util.CUSTOMER_MEASUREMENT);

        isCustomer = origin == Util.CUSTOMER_MEASUREMENT;
        isEmp = origin == Util.EMP_MEASUREMENT;

        if (isCustomer) { oldCustomer = Objects.requireNonNull(getIntent().getExtras()).getParcelable("oldCustomer"); }
        if (isEmp) { oldEmp = Objects.requireNonNull(getIntent().getExtras()).getParcelable("emp"); }

    }

    public void onClickProduct(View view){
        switch (view.getId()){
            case R.id.shirt_measurement_btn:
                // take to shirt measurements
                Intent intent = new Intent(SelectProductActivity.this, ShirtMeasurementActivity.class);
                intent.putExtra("origin",origin);
                if (isEmp) {intent.putExtra("oldEmp",oldEmp);}
                if (isCustomer) {intent.putExtra("oldCustomer",oldCustomer);}
                startActivity(intent);
                break;
            case R.id.pant_measurement_btn:
                //take to pant measurements
                Intent intent1 = new Intent(SelectProductActivity.this, PantMeasurementActivity.class);
                intent1.putExtra("origin",origin);
                if (isEmp) {intent1.putExtra("oldEmp",oldEmp);}
                if (isCustomer) {intent1.putExtra("oldCustomer",oldCustomer);}
                startActivity(intent1);
                break;
        }
    }

    //    for getting back to previous activity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
        } else return false;
        return true;
    }
}
