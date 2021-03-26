package com.example.mtailor.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.example.mtailor.R;
import com.example.mtailor.utils.LanguageHelper;

import java.util.Objects;

public class AboutAppActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

//        go back to home
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Resources resources = LanguageHelper.updateLanguage(this);
        TextView instructionsText = findViewById(R.id.instructions_text);

        instructionsText.setText(resources.getString(R.string.about_instructions));

    }
}