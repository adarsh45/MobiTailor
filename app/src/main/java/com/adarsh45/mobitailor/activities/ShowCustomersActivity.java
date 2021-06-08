package com.adarsh45.mobitailor.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.adarsh45.mobitailor.R;
import com.adarsh45.mobitailor.adapters.CustomerAdapter;
import com.adarsh45.mobitailor.adapters.OrgAdapter;
import com.adarsh45.mobitailor.pojo.Customer;
import com.adarsh45.mobitailor.pojo.Org;
import com.adarsh45.mobitailor.pojo.PaymentDetails;
import com.adarsh45.mobitailor.utils.LanguageHelper;
import com.adarsh45.mobitailor.utils.ResultDialog;
import com.adarsh45.mobitailor.utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Objects;

public class ShowCustomersActivity extends AppCompatActivity {

    private static final String TAG = "ShowCustomersActivity";

    private FirebaseDatabase myDB;
    private DatabaseReference rootRef, orgRef, customerRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    String UID;
    private long limit = 25;
    private boolean isPaidMember = false;
    byte origin;
    private long customerCount;

    ArrayList<Customer> customerArrayList;
    ArrayList<Org> orgArrayList;
    RecyclerView recyclerView;
    TextView emptyTextView;

    private Resources resources;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_customers);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialize();
        checkIsPaidMember();

        origin = getIntent().getByteExtra("origin", Util.SHOW_CUSTOMERS);

        resources = LanguageHelper.updateLanguage(this);
        emptyTextView.setText(resources.getString(R.string.customer_list_empty));

        switch (origin){
            case Util.SHOW_CUSTOMERS:
                getSupportActionBar().setTitle(resources.getString(R.string.view_customers));
                createFAB();
                getCustomers();
                break;
            case Util.TAKE_MEASUREMENTS:
                getSupportActionBar().setTitle(resources.getString(R.string.measurement_select_customer));
                findViewById(R.id.fab).setVisibility(View.GONE);
                getCustomers();
                break;
            case Util.NEW_ORDER:
                getSupportActionBar().setTitle(resources.getString(R.string.new_order_select_customer));
                findViewById(R.id.fab).setVisibility(View.GONE);
                getCustomers();
                break;
            case Util.SHOW_ORDERS:
                getSupportActionBar().setTitle(resources.getString(R.string.show_orders_select_customer));
                findViewById(R.id.fab).setVisibility(View.GONE);
                getCustomers();
                break;
            case Util.SHOW_ORGANIZATIONS:
                createFAB();
                getSupportActionBar().setTitle(resources.getString(R.string.organizations));
                getOrganizations();
                break;
            case Util.SHOW_EMPLOYEES:
                getSupportActionBar().setTitle(resources.getString(R.string.select_org));
                getOrganizations();
                break;
        }

    }


    private void checkIsPaidMember() {
        final DatabaseReference paymentDetailsRef = rootRef.child(UID).child("PaymentDetails");
//        offline sync
        paymentDetailsRef.keepSynced(true);
        paymentDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("paidMember") && snapshot.hasChild("limit")){
//                    payment details
                    PaymentDetails paymentDetails = snapshot.getValue(PaymentDetails.class);
                    assert paymentDetails != null;
                    limit = paymentDetails.getLimit();
                    isPaidMember = paymentDetails.isPaidMember();

                } else {
//                    no data about payment details in DB
//                    save new data in DB now
                    PaymentDetails paymentDetails = new PaymentDetails
                            (UID, Util.getCurrentDate(), null, false, 25);
                    paymentDetailsRef.setValue(paymentDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
//                                registration date is saved in DB
                                Log.d(TAG, "onComplete: NEW Payment Details saved in DB");
                            } else {
                                Toast.makeText(ShowCustomersActivity.this, "Some data is not stored in Databse! Please Logout & Retry", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowCustomersActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void createFAB() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fabIntent = new Intent();

//                check if payment done
                if (!isPaidMember && customerCount >= limit){
                    ResultDialog dialog = new ResultDialog(ShowCustomersActivity.this, false, resources.getString(R.string.limit_error));
                    dialog.show(getSupportFragmentManager(),"Result");
                    return;
                }

                if (origin == Util.SHOW_CUSTOMERS){
                    fabIntent = new Intent(ShowCustomersActivity.this, NewCustomerActivity.class);
                    fabIntent.putExtra("origin", Util.NEW_CUSTOMER);
                } else if (origin == Util.SHOW_ORGANIZATIONS) {
                    fabIntent = new Intent(ShowCustomersActivity.this, NewOrganizationActivity.class);
                    fabIntent.putExtra("origin", Util.NEW_ORGANIZATION);
                }
                startActivity(fabIntent);

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
        assert currentUser != null;
        UID = currentUser.getUid();
        orgRef = rootRef.child(UID).child("Organizations");
        customerRef = rootRef.child(UID).child("Customers");

//        enable offline sync for db ref
        rootRef.keepSynced(true);
        orgRef.keepSynced(true);
        customerRef.keepSynced(true);

        emptyTextView = findViewById(R.id.empty_text);
        recyclerView = findViewById(R.id.recycler_view);
//        progressBar = findViewById(R.id.customers_progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

//    for getting back to previous activity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
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

                        if (origin == Util.SHOW_ORGANIZATIONS){
                            recyclerView.setAdapter(new OrgAdapter(orgArrayList, Util.UPDATE_ORGANIZATION));
                        } else if (origin == Util.SHOW_EMPLOYEES){
                            recyclerView.setAdapter(new OrgAdapter(orgArrayList, Util.NEW_EMPLOYEE));
                        }
                    } else {
                        emptyTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
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
                        customerCount = dataSnapshot.getChildrenCount();
                        customerArrayList = new ArrayList<>();
                        for (DataSnapshot mySnap : dataSnapshot.getChildren()){
                            Customer myCustomer = mySnap.getValue(Customer.class);
                            customerArrayList.add(myCustomer);
                        }

                        recyclerView.setAdapter(new CustomerAdapter(customerArrayList, origin));
                    }
                    else {
                        emptyTextView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
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

        recyclerView.setAdapter(new CustomerAdapter(filterList, origin));
    }

    private void searchOrg(String newText){
        ArrayList<Org> filterList = new ArrayList<>();
        for (Org filterOrg : orgArrayList){
            if (filterOrg.getOrgName().toLowerCase().contains(newText.toLowerCase()) || filterOrg.getOrgOwner().toLowerCase().contains(newText.toLowerCase())){
                filterList.add(filterOrg);
            }
        }

        recyclerView.setAdapter(new OrgAdapter(filterList, origin));
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
                if (origin == Util.SHOW_CUSTOMERS || origin == Util.TAKE_MEASUREMENTS || origin == Util.NEW_ORDER || origin == Util.SHOW_ORDERS){
                    searchCustomer(newText);
                } else if (origin == Util.SHOW_ORGANIZATIONS || origin == Util.SHOW_EMPLOYEES){
                    searchOrg(newText);
                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
