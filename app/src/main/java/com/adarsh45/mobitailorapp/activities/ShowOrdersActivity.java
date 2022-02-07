package com.adarsh45.mobitailorapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.adarsh45.mobitailorapp.R;
import com.adarsh45.mobitailorapp.adapters.OrdersAdapter;
import com.adarsh45.mobitailorapp.pojo.Customer;
import com.adarsh45.mobitailorapp.pojo.Order;
import com.adarsh45.mobitailorapp.utils.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowOrdersActivity extends AppCompatActivity {
    private static final String TAG = "ShowOrdersActivity";

//    views
    private RecyclerView rvShowOrders;
    private TextView tvNoResults;

//    objects
    private Customer customer;

//    firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_orders);

        initViews();
        getCustomer();
        fetchOrders();
    }

    public void onClickFabNewOrder(View view){
        Intent newOrderIntent = new Intent(ShowOrdersActivity.this, NewOrderActivity.class);
        newOrderIntent.putExtra("origin", Util.NEW_ORDER);
        newOrderIntent.putExtra("oldCustomer", customer);
        startActivity(newOrderIntent);
    }

    private void getCustomer() {
        customer = getIntent().getExtras().getParcelable("oldCustomer");
        if (customer == null){
            Toast.makeText(this, "something went wrong! Please restart the app!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "getCustomer: customer not found!");
        } else {
            getSupportActionBar().setTitle(customer.getCustomerName());
        }
    }

    private void fetchOrders() {
        if (customer == null){
            Toast.makeText(this, "Customer not found! Please retry!", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference ordersRef = rootRef.child(currentUser.getUid()).child("Orders").child(customer.getCustomerID());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    ArrayList<Order> orderArrayList = new ArrayList<>();
                    for (DataSnapshot snap: snapshot.getChildren()){
                        orderArrayList.add(snap.getValue(Order.class));
                        Log.d(TAG, "onDataChange: "+ snap.getValue(Order.class).getOrder_ref_no());
                    }
                    rvShowOrders.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    rvShowOrders.setAdapter(new OrdersAdapter(getApplicationContext(), getSupportFragmentManager(), customer, orderArrayList));
                } else {
                    tvNoResults.setVisibility(View.VISIBLE);
                    rvShowOrders.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
                Toast.makeText(ShowOrdersActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initViews() {
        rvShowOrders = findViewById(R.id.rv_show_orders);
        tvNoResults = findViewById(R.id.empty_text_orders);
    }
}