package com.example.mtailor.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.example.mtailor.R;
import com.example.mtailor.utils.Util;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        } else return false;
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Util.settingsSPFileName, MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();

            SwitchPreference switchPreference = findPreference("organizations-section");
            if (switchPreference != null){
                switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        boolean showOrgSection = (Boolean) newValue;
                        editor.putBoolean(Util.booleanShowOrgSection, showOrgSection);
                        editor.apply();
                        Toast.makeText(requireActivity(), "Settings saved", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }

            final ListPreference languagePreference = findPreference("select_language");
            if (languagePreference != null){
                languagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        int index = Integer.parseInt(newValue.toString());
                        String localeLang="";
                        switch (index){
                            case 0:
                                localeLang ="en";
                                break;
                            case 1:
                                localeLang ="mr_";
                                break;
                            case 2:
                                localeLang ="mr_IN";
                                break;
                            default: localeLang = "en";
                        }
                        editor.putString(Util.appLanguage, localeLang);
                        editor.apply();
                        Toast.makeText(requireActivity(), "App language is: "+ languagePreference.getEntries()[index], Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }
        }
    }
}