package com.example.mtailor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.mtailor.R;
import com.example.mtailor.pojo.Profile;
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

public class ProfileActivity extends AppCompatActivity {

    private FirebaseDatabase myDB;
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String UID;

    private EditText editShopName, editOwnerName, editShopAddress;

    @Override
    protected void onStart() {
        super.onStart();

        rootRef.child(UID).child("Profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Profile profile = dataSnapshot.getValue(Profile.class);
                    editShopName.setText(profile.getShopName());
                    editOwnerName.setText(profile.getOwnerName());
                    editShopAddress.setText(profile.getShopAddress());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showSnackbar(databaseError.getMessage());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initialize();

    }

    private void initialize() {
        //init database and references
        myDB = FirebaseDatabase.getInstance();
        rootRef = myDB.getReference().child("Users");

        //init firebase auth
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        UID = currentUser.getUid();
        //finding views
        editShopName = findViewById(R.id.edit_shop_name);
        editOwnerName = findViewById(R.id.edit_owner_name);
        editShopAddress = findViewById(R.id.edit_shop_address);
    }

    public void onClickProfile(View view){
        if (view.getId() == R.id.register_btn){
            registerProfile(this);
        }
    }

    public void showSnackbar(CharSequence text){
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.profile_layout),text,Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private static void registerProfile(final ProfileActivity profileActivity) {
        String shopName = profileActivity.editShopName.getText().toString().trim();
        String ownerName = profileActivity.editOwnerName.getText().toString().trim();
        String shopAddress = profileActivity.editShopAddress.getText().toString().trim();

        Profile profile = new Profile(profileActivity.UID, shopName, ownerName, shopAddress, profileActivity.currentUser.getPhoneNumber());


        profileActivity.rootRef.child(profileActivity.UID).child("Profile").setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(profileActivity, HomeActivity.class);
                    profileActivity.startActivity(intent);
                    profileActivity.finish();
                } else profileActivity.showSnackbar("Something went wrong");
            }
        });


    }


}
