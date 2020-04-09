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
import android.widget.ProgressBar;

import com.example.mtailor.R;
import com.example.mtailor.adapters.CustomerAdapter;
import com.example.mtailor.adapters.OrgAdapter;
import com.example.mtailor.pojo.Customer;
import com.example.mtailor.pojo.Org;
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

import static com.example.mtailor.adapters.OrgAdapter.ADD_NEW_EMPLOYEE;
import static com.example.mtailor.adapters.OrgAdapter.SHOW_ORG;

public class ShowCustomersActivity extends AppCompatActivity {

    int SHOW_CUSTOMERS = 1;
    int TAKE_MEASUREMENT = 2;

    private FirebaseDatabase myDB;
    private DatabaseReference rootRef, orgRef, customerRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    String UID;
    String origin;

    ArrayList<Customer> customerArrayList;
    ArrayList<Org> orgArrayList;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_customers);

        initialize();

        origin = getIntent().getStringExtra("origin");

        switch (origin){
            case "showCustomers":
                getSupportActionBar().setTitle("View Customers");
                createFAB();
                getCustomers();
                break;
            case "measurement":
                getSupportActionBar().setTitle("Select Customer");
                findViewById(R.id.fab).setVisibility(View.GONE);
                getCustomers();
                break;
            case "organization":
                createFAB();
                getSupportActionBar().setTitle("Organizations");
                getOrganizations();
                break;
            case "employee":
                getSupportActionBar().setTitle("Select Organization");
                getOrganizations();
                break;
        }

    }

    private void createFAB() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (origin.equals("showCustomers")){
                    Intent intent = new Intent(ShowCustomersActivity.this, NewCustomerActivity.class);
                    intent.putExtra("origin", "newCustomer");
                    startActivity(intent);
                } else if (origin.equals("organization")) {
                    Intent intent = new Intent(ShowCustomersActivity.this, NewOrganizationActivity.class);
                    intent.putExtra("origin", "newOrg");
                    startActivity(intent);
                }

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

        //init database and references
        myDB = FirebaseDatabase.getInstance();
        rootRef = myDB.getReference().child("Users");

        //init firebase auth
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        UID = currentUser.getUid();
        orgRef = rootRef.child(UID).child("Organizations");
        customerRef = rootRef.child(UID).child("Customers");

        recyclerView = findViewById(R.id.recycler_view);
//        progressBar = findViewById(R.id.customers_progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

//    get organizations data and display in RV
    private void getOrganizations() {
        if (orgRef != null){
            orgRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        orgArrayList = new ArrayList<>();
                        for (DataSnapshot mySnap : dataSnapshot.getChildren()){
                            Org myOrg = mySnap.getValue(Org.class);
                            orgArrayList.add(myOrg);
                        }

                        if (origin.equals("organization")){
                            recyclerView.setAdapter(new OrgAdapter(orgArrayList, SHOW_ORG));
                        } else if (origin.equals("employee")){
                            recyclerView.setAdapter(new OrgAdapter(orgArrayList, ADD_NEW_EMPLOYEE));
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


//    get customers data and display in recyclerview
    private void getCustomers() {
        if (customerRef != null){
//            progressBar.setVisibility(View.VISIBLE);
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
//        progressBar.setVisibility(View.GONE);
    }

//    search bar and action bar code

    private void searchCustomer(String newText){
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

    private void searchOrg(String newText){
        ArrayList<Org> filterList = new ArrayList<>();
        for (Org filterOrg : orgArrayList){
            if (filterOrg.getOrgName().toLowerCase().contains(newText.toLowerCase()) || filterOrg.getOrgOwner().toLowerCase().contains(newText.toLowerCase())){
                filterList.add(filterOrg);
            }
        }

        if (origin.equals("organization")){
            recyclerView.setAdapter(new OrgAdapter(filterList, SHOW_ORG));
        } else if (origin.equals("newEmployee")){
            recyclerView.setAdapter(new OrgAdapter(filterList, ADD_NEW_EMPLOYEE));
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
                if (origin.equals("showCustomers") || origin.equals("measurement")){
                    searchCustomer(newText);
                } else if (origin.equals("organization") || origin.equals("newEmployee")){
                    searchOrg(newText);
                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
