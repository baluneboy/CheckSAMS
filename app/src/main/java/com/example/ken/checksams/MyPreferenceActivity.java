package com.example.ken.checksams;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Created by pims on 6/19/15.
 */
/*public class MyPreferenceActivity extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {*/

public class MyPreferenceActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements
            SharedPreferences.OnSharedPreferenceChangeListener {

        public static final String KEY_PREF_PLAY_ALARM = "alarmOnCheckBox";

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            if (key.equals(KEY_PREF_PLAY_ALARM))
            {
                // Set summary to be the user-description for the selected value
                Preference alarmOnPref = findPreference(key);
                boolean alarmOnCheckBox = sharedPreferences.getBoolean("alarmOnCheckBox", false);
                String tf = String.valueOf(alarmOnCheckBox);
                alarmOnPref.setSummary(tf);
            }

        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

    }

}
