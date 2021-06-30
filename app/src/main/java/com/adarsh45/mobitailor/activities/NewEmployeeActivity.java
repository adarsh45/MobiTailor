package com.adarsh45.mobitailor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adarsh45.mobitailor.R;
import com.adarsh45.mobitailor.adapters.EmpAdapter;
import com.adarsh45.mobitailor.pojo.Emp;
import com.adarsh45.mobitailor.pojo.Org;
import com.adarsh45.mobitailor.utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    private static final String TAG = "NewEmployeeActivity";

    private FirebaseDatabase myDB;
    private DatabaseReference rootRef, empRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String UID;
    private byte origin;
    private Org myOrg;

    private LinearLayout rootLayout;
    private TextView orgNameText;
    private EditText editNewEmployee;
    private Button btnAddEmployee;
    private RecyclerView rvEmployee;

    ArrayList<Emp> empArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_employee);

        origin = getIntent().getByteExtra("origin", Util.NEW_EMPLOYEE);
        getSupportActionBar().setTitle("Add Employee");

        if (origin == Util.NEW_EMPLOYEE){
            myOrg = getIntent().getParcelableExtra("oldOrg");
            initialize();
            orgNameText.setText(myOrg.getOrgName());

            addNewEmp();
            showEmployees();
            searchEmployee();
        }

    }

    private void initialize() {
//        adding back button on toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        firebase setup
        myDB = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        UID = currentUser.getUid();
        rootRef = myDB.getReference().child("Users").child(UID);
        empRef = rootRef.child("Employees").child(myOrg.getOrgID());

//        finding views
        rootLayout = findViewById(R.id.new_emp_layout);
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
                            Util.showSnackBar(rootLayout, "Employee Added Successfully");
                        } else Util.showSnackBar(rootLayout,"Something went wrong!");
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
                rvEmployee.setAdapter(new EmpAdapter(empArrayList, new EmpAdapter.EmployeeClick() {
                    @Override
                    public void onEmployeeCardClick(int position) {
                        openEmployeeMeasures(empArrayList.get(position));
                    }

                    @Override
                    public void onEmpDeleteClick(int position) {
                        deleteEmployee(empArrayList.get(position));
                    }
                }));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Util.showSnackBar(rootLayout,databaseError.getMessage());
            }
        });
    }

    private void deleteEmployee(Emp emp) {
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(NewEmployeeActivity.this);
        deleteDialog.setTitle("Delete Employee")
                .setMessage("Are you sure you want to delete " + emp.getEmpName() + " ?")
                .setIcon(R.drawable.ic_delete_black)
                .setPositiveButton("DELETE", (dialog, which) -> {
                    deleteEmpFromDB(emp, dialog);
                })
                .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
                .setCancelable(false);

        deleteDialog.show();

    }

    private void deleteEmpFromDB(Emp emp, DialogInterface dialog) {
//        actually removing the data from db
        empRef.child(emp.getEmpID()).removeValue();

//        listening for change in db
        empRef.child(emp.getEmpID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
//                    employee not deleted, show error
                    dialog.dismiss();
                    Util.showSnackBar(rootLayout, "Record of " + emp.getEmpName() + " could not be deleted!");
                } else {
//                    employee record deleted successfully
//                    now see if that emp had any measurements and delete them too
                    deleteEmpMeasurement(emp, dialog);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Util.showSnackBar(rootLayout, error.getMessage());
                Log.d(TAG, "onCancelled: "+ error.getMessage());
                dialog.dismiss();
            }
        });

    }

    private void openEmployeeMeasures(Emp emp) {
        Intent intent = new Intent(NewEmployeeActivity.this, SelectProductActivity.class);
        intent.putExtra("origin", Util.EMP_MEASUREMENT);
        intent.putExtra("emp",emp);
        startActivity(intent);
    }

    private void deleteEmpMeasurement(Emp emp, DialogInterface dialog) {
        DatabaseReference empMeasurementRef = rootRef.child("Measurements").child(emp.getEmpID());
        empMeasurementRef.removeValue();

        empMeasurementRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialog.dismiss();
                if (snapshot.exists()){
                    Util.showSnackBar(rootLayout, emp.getEmpName() + "'s measurements could not be deleted!");
                } else {
                    Util.showSnackBar(rootLayout, emp.getEmpName() + "'s record deleted successfully!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Util.showSnackBar(rootLayout, error.getMessage());
                dialog.dismiss();
            }
        });

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
        rvEmployee.setAdapter(new EmpAdapter(filterList, new EmpAdapter.EmployeeClick() {
            @Override
            public void onEmployeeCardClick(int position) {
                openEmployeeMeasures(filterList.get(position));
            }

            @Override
            public void onEmpDeleteClick(int position) {
                deleteEmployee(filterList.get(position));
            }
        }));
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

    //    for getting back to previous activity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
