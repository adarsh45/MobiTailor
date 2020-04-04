package com.example.mtailor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.mtailor.R;
import com.example.mtailor.adapters.CustomerAdapter;
import com.example.mtailor.pojo.Customer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowCustomersActivity extends AppCompatActivity {

    int SHOW_CUSTOMERS = 1;
    int TAKE_MEASUREMENT = 2;

    DatabaseReference rootRef, customerRef;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    String UID;
    String origin;

    ArrayList<Customer> customerArrayList;
    RecyclerView recyclerView;

    @Override
    protected void onStart() {
        super.onStart();
        getSupportActionBar().setTitle("View Customers");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_customers);

        origin = getIntent().getStringExtra("origin");

        if (origin.equals("showCustomers")){
            createFAB();
        } else if (origin.equals("measurement")){
            getSupportActionBar().setTitle("Select Customer");
            findViewById(R.id.fab).setVisibility(View.GONE);
        }

        initialize();
        getData();
    }

    private void createFAB() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowCustomersActivity.this, NewCustomerActivity.class);
                intent.putExtra("origin", "newCustomer");
                startActivity(intent);
            }
        });
    }

    public void showSnackbar(CharSequence text){
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.show_customer_layout),text,Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private void initialize() {

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        UID = currentUser.getUid();

        rootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
        customerRef = rootRef.child("Customers");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getData() {
        if (customerRef != null){
            customerRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        customerArrayList = new ArrayList<>();
                        for (DataSnapshot mySnap : dataSnapshot.getChildren()){
                            Customer myCustomer = mySnap.getValue(Customer.class);
                            customerArrayList.add(myCustomer);
                        }

                        if (origin.equals("showCustomers")){
                            recyclerView.setAdapter(new CustomerAdapter(customerArrayList, SHOW_CUSTOMERS));
                        } else if (origin.equals("measurement")){
                            recyclerView.setAdapter(new CustomerAdapter(customerArrayList, TAKE_MEASUREMENT));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showSnackbar(databaseError.getMessage());
                }
            });
        }
    }

    private void search(String newText){
        ArrayList<Customer> filterList = new ArrayList<>();
        for (Customer filterCustomer : customerArrayList){
            if (filterCustomer.getCustomerName().toLowerCase().contains(newText.toLowerCase()) || filterCustomer.getCustomerMobile().toLowerCase().contains(newText.toLowerCase())){
                filterList.add(filterCustomer);
            }
        }

        if (origin.equals("showCustomers")){
            recyclerView.setAdapter(new CustomerAdapter(filterList, SHOW_CUSTOMERS));
        } else if (origin.equals("measurement")){
            recyclerView.setAdapter(new CustomerAdapter(filterList, TAKE_MEASUREMENT));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        androidx.appcompat.widget.SearchView searchView1 = (androidx.appcompat.widget.SearchView) myActionMenuItem.getActionView();

        searchView1.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
