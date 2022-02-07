package com.adarsh45.mobitailorapp.utils;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class OfflineFirebase extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        enable offline storing in firebase across the app
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
