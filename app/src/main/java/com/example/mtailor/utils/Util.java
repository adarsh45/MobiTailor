package com.example.mtailor.utils;

import android.app.Application;
import android.content.Context;
import android.view.View;

import com.example.mtailor.R;
import com.google.android.material.snackbar.Snackbar;

public class Util {
    private Util(){}

    public static void showSnackbar(View rootView, CharSequence text){
        final Snackbar snackbar = Snackbar.make(rootView,text,Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

}
