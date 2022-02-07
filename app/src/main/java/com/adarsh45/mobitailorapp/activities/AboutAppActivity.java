package com.adarsh45.mobitailorapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adarsh45.mobitailorapp.R;
import com.adarsh45.mobitailorapp.pojo.PaymentDetails;
import com.adarsh45.mobitailorapp.utils.LanguageHelper;
import com.adarsh45.mobitailorapp.utils.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AboutAppActivity extends AppCompatActivity {

    private ConstraintLayout rootLayout;
    private LinearLayout layoutPaidMember;
    private TextView textPurchaseApp;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseDatabase myDB = FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = myDB.getReference("Users").child(currentUser.getUid());

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        rootLayout = findViewById(R.id.about_app_root_layout);
        layoutPaidMember = findViewById(R.id.layout_paid_member);
        textPurchaseApp = findViewById(R.id.instructions_text);

        checkIfPaidMember();

//        go back to home
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Resources resources = LanguageHelper.updateLanguage(this);
        textPurchaseApp.setText(resources.getString(R.string.about_instructions));

    }

    private void checkIfPaidMember() {
        rootRef.child("PaymentDetails").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    PaymentDetails paymentDetails = snapshot.getValue(PaymentDetails.class);
                    if (paymentDetails.isPaidMember()){
                        layoutPaidMember.setVisibility(View.VISIBLE);
                        textPurchaseApp.setVisibility(View.GONE);
                    } else {
                        layoutPaidMember.setVisibility(View.GONE);
                        textPurchaseApp.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Util.showSnackBar(rootLayout, error.getMessage());
            }
        });
    }
}