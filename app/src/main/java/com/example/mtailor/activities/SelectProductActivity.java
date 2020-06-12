package com.example.mtailor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.mtailor.R;
import com.example.mtailor.pojo.Customer;
import com.example.mtailor.pojo.Emp;

public class SelectProductActivity extends AppCompatActivity {

    Customer oldCustomer;
    Emp oldEmp;
    String origin;
    boolean isEmp, isCustomer;

//    origin = customerMeasurement (from ShowCustomers Activity & CustomerAdapter)
//    origin = empMeasurement (from ShowCustomers Activity & EmpAdapter )
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);

        getSupportActionBar().setTitle("Select Item");
//        adding back button on toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        origin = getIntent().getStringExtra("origin");

        isEmp = origin.equals("empMeasurement");
        isCustomer = origin.equals("customerMeasurement");

        if (isCustomer) { oldCustomer = getIntent().getExtras().getParcelable("oldCustomer"); }
        if (isEmp) { oldEmp = getIntent().getExtras().getParcelable("emp"); }

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
        }
        return super.onOptionsItemSelected(item);
    }
}
