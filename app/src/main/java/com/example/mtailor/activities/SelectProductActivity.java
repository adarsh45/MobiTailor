package com.example.mtailor.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mtailor.R;
import com.example.mtailor.pojo.Customer;

public class SelectProductActivity extends AppCompatActivity {

    Customer oldCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);

        getSupportActionBar().setTitle("Select Item");

        //get whole customer object
        oldCustomer = getIntent().getExtras().getParcelable("oldCustomer");

    }

    public void onClickProduct(View view){
        switch (view.getId()){
            case R.id.shirt_measurement_btn:
                // take to shirt measurements
                Intent intent = new Intent(SelectProductActivity.this, ShirtMeasurementActivity.class);
                intent.putExtra("oldCustomer",oldCustomer);
                startActivity(intent);
                break;
            case R.id.pant_measurement_btn:
                //take to pant measurements
                Intent intent1 = new Intent(SelectProductActivity.this, PantMeasurementActivity.class);
                intent1.putExtra("oldCustomer",oldCustomer);
                startActivity(intent1);
                break;
        }
    }
}
