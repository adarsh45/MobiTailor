package com.adarsh45.mobitailorapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.adarsh45.mobitailorapp.R;
import com.adarsh45.mobitailorapp.pojo.Profile;
import com.adarsh45.mobitailorapp.utils.LanguageHelper;
import com.adarsh45.mobitailorapp.utils.Util;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseDatabase myDB;
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private NavigationView navigationView;
    private View header;
    private TextView toolBarText, initialCharText, shopNameText, ownerNameText;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;

    private String UID;


    @Override
    protected void onStart() {
        getMySharedPreference();
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initialize();
        setTexts();
        LanguageHelper.updateLanguage(this);
    }

    private void getMySharedPreference() {
//        SharedPreferences sharedPreferences = getSharedPreferences(Util.settingsSPFileName, MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences != null){
//            boolean showOrgSection = sharedPreferences.getBoolean(Util.booleanShowOrgSection, false);
            boolean showOrgSection = sharedPreferences.getBoolean("organizations-section", false);
            if (showOrgSection){
                findViewById(R.id.text_org_section).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_org_section).setVisibility(View.VISIBLE);
//                Log.d("TAG", "getMySharedPreference: VISIBILITY TRUE FOR VIEWS");
            }
        }

    }

    private void setTexts(){
        toolBarText = findViewById(R.id.toolbar_title);
        initialCharText = header.findViewById(R.id.navbar_initial_char);
        shopNameText = header.findViewById(R.id.nav_shop_name);
        ownerNameText = header.findViewById(R.id.nav_owner_name);

        rootRef.child(UID).child("Profile").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Profile profile = dataSnapshot.getValue(Profile.class);

                    assert profile != null;
                    char initialChar = profile.getShopName().charAt(0);

//                    Setting names of shop,owner and logo in navigation bar's header layout
                    initialCharText.setText(String.valueOf(initialChar));
                    shopNameText.setText(profile.getShopName());
                    ownerNameText.setText(profile.getOwnerName());

                    Objects.requireNonNull(getSupportActionBar()).setTitle(null);
                    toolBarText.setText(profile.getShopName());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showSnackbar(databaseError.getMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initialize() {

        //init database and references
        myDB = FirebaseDatabase.getInstance();
        rootRef = myDB.getReference().child("Users");

        //init firebase auth
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        UID = currentUser.getUid();

        //setting new toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //init navigationView for onClicks on menu items
        navigationView = findViewById(R.id.navigation_layout);
        header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        //init drawer_layout and toggle_button
        mDrawerLayout = findViewById(R.id.home_drawer_layout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    public void showSnackbar(CharSequence text){
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.home_main_layout),text,Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    public void onClickHome(View view){
        Intent intent;
        switch (view.getId()){
            case R.id.show_customer_btn:
                //new customer register
                intent = new Intent(HomeActivity.this, ShowCustomersActivity.class);
                intent.putExtra("origin",Util.SHOW_CUSTOMERS);
                startActivity(intent);
                break;
            case R.id.show_measurements_btn:
                //show for measurement
                intent = new Intent(HomeActivity.this, ShowCustomersActivity.class);
                intent.putExtra("origin", Util.TAKE_MEASUREMENTS);
                startActivity(intent);
                break;
            case R.id.new_order_btn:
                //take new order
                intent = new Intent(HomeActivity.this, ShowCustomersActivity.class);
                intent.putExtra("origin", Util.NEW_ORDER);
                startActivity(intent);
                break;
            case R.id.show_orders_btn:
                //view previous order
                intent = new Intent(HomeActivity.this, ShowCustomersActivity.class);
                intent.putExtra("origin", Util.SHOW_ORDERS);
                startActivity(intent);
                break;
            case R.id.new_org_btn:
                //add new organization
                intent = new Intent(HomeActivity.this, ShowCustomersActivity.class);
                intent.putExtra("origin",Util.SHOW_ORGANIZATIONS);
                startActivity(intent);
                break;
            case R.id.new_employee_btn:
                //add new employee within the organization
                intent = new Intent(HomeActivity.this, ShowCustomersActivity.class);
                intent.putExtra("origin",Util.SHOW_EMPLOYEES);
                startActivity(intent);
                break;
            default:
                Toast.makeText(this, "Sorry, this section is under construction!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater homeMenuInflater = getMenuInflater();
        homeMenuInflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_feedback:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + getPackageName())));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
                return true;
//            case R.id.menu_update:
//                Toast.makeText(this, "Update the app", Toast.LENGTH_SHORT).show();
//                return true;
            case R.id.menu_about_app:
                startActivity(new Intent(HomeActivity.this, AboutAppActivity.class));
                return true;
        }

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.menu_profile:
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.menu_settings:
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                break;
            case R.id.menu_main_about:
                startActivity(new Intent(HomeActivity.this, AboutAppActivity.class));
                break;
            case R.id.menu_logout:
                mAuth.signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
