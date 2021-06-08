package com.adarsh45.mobitailor.utils;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.adarsh45.mobitailor.activities.ShirtMeasurementActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OfflineFirebase extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        enable offline storing in firebase across the app
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
