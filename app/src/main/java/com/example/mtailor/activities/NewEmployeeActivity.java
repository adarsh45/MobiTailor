package com.example.mtailor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mtailor.R;
import com.example.mtailor.adapters.EmpAdapter;
import com.example.mtailor.pojo.Emp;
import com.example.mtailor.pojo.Org;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class NewEmployeeActivity extends AppCompatActivity {

    private FirebaseDatabase myDB;
    private DatabaseReference rootRef, empRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String origin, UID;
    private Org myOrg;

    private TextView orgNameText;
    private EditText editNewEmployee;
    private Button btnAddEmployee;
    private RecyclerView rvEmployee;

    ArrayList<Emp> empArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_employee);

        origin = getIntent().getStringExtra("origin");
        getSupportActionBar().setTitle("Add Employee");

        if (origin.equals("employee")){
            myOrg = getIntent().getParcelableExtra("oldOrg");
            initialize();
            orgNameText.setText(myOrg.getOrgName());

            addNewEmp();
            showEmployees();
            searchEmployee();
        }

    }

    private void initialize() {
//        firebase setup
        myDB = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        UID = currentUser.getUid();
        rootRef = myDB.getReference().child("Users").child(UID);
        empRef = rootRef.child("Employees").child(myOrg.getOrgID());

//        finding views
        orgNameText = findViewById(R.id.text_view_org_name);
        editNewEmployee = findViewById(R.id.edit_employee_name);
        btnAddEmployee = findViewById(R.id.btn_add_employee);
        rvEmployee = findViewById(R.id.recycler_view_employees);

        rvEmployee.setLayoutManager(new LinearLayoutManager(this));
    }

    private void addNewEmp() {
        btnAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String empName = editNewEmployee.getText().toString().trim();
                String empID = empRef.push().getKey();

                Emp emp = new Emp(empID,empName);

                empRef.child(empID).setValue(emp).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            editNewEmployee.setText(null);
                            showSnackbar("Employee Added Successfully");
                        } else showSnackbar("Something went wrong!");
                    }
                });
            }
        });
    }

    private void showEmployees(){
        empArrayList = new ArrayList<>();
        empRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                empArrayList.clear();
                for (DataSnapshot myData : dataSnapshot.getChildren()){
                    Emp myEmp = myData.getValue(Emp.class);
                    empArrayList.add(myEmp);
                }
                Collections.reverse(empArrayList);
                rvEmployee.setAdapter(new EmpAdapter(empArrayList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showSnackbar(databaseError.getMessage());
            }
        });


    }

    public void showSnackbar(CharSequence text){
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.new_emp_layout),text,Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    public void searchEmployee(){
        editNewEmployee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void search(String newText){
        ArrayList<Emp> filterList = new ArrayList<>();
        for (Emp filterEmp : empArrayList){
            if (filterEmp.getEmpName().toLowerCase().contains(newText.toLowerCase())){
                filterList.add(filterEmp);
            }
        }
        rvEmployee.setAdapter(new EmpAdapter(filterList));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        androidx.appcompat.widget.SearchView searchView1 = (androidx.appcompat.widget.SearchView) myActionMenuItem.getActionView();

        searchView1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
