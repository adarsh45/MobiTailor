package com.adarsh45.mobitailor.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.adarsh45.mobitailor.R;

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

//            final SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Util.settingsSPFileName, MODE_PRIVATE);
//            final SharedPreferences.Editor editor = sharedPreferences.edit();

//            SwitchPreference switchPreference = findPreference("organizations-section");
//            if (switchPreference != null){
//                switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                    @Override
//                    public boolean onPreferenceChange(Preference preference, Object newValue) {
//                        boolean showOrgSection = (Boolean) newValue;
//                        editor.putBoolean(Util.booleanShowOrgSection, showOrgSection);
//                        editor.apply();
//                        Toast.makeText(requireActivity(), "Settings saved", Toast.LENGTH_SHORT).show();
//                        return true;
//                    }
//                });
//            }

//            final ListPreference languagePreference = findPreference("select_language");
//            if (languagePreference != null){
//                languagePreference.setTitle("Select Preferred Lang");
//                languagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                    @Override
//                    public boolean onPreferenceChange(Preference preference, Object newValue) {
//                        String localeLang = newValue.toString();
//                        editor.putString(Util.appLanguage, localeLang);
//                        editor.apply();
//                        return true;
//                    }
//                });
//            }
        }
    }
}