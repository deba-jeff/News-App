package com.example.newsapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }


    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_preference);

            // The findPreference determines the type of preference using the key
            Preference pageSize = findPreference("page_size_key");

            // Sets the new value to be displayed below the preference name
            bindPreferenceValueToSummary(pageSize);

            Preference orderBy = findPreference("order_by_key");
            bindPreferenceValueToSummary(orderBy);

            Preference section = findPreference("section_key");
            bindPreferenceValueToSummary(section);
        }



        /**
         * Displays the new value below the preference
         */
        private void bindPreferenceValueToSummary(Preference newPreference){

            if (newPreference instanceof EditTextPreference){
                // Listens for changes on the Preference
                newPreference.setOnPreferenceChangeListener(this);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(newPreference.getContext());

                // Get the new value associated with the key
                String editTextPreferenceString = sharedPref.getString(newPreference.getKey(), "10");

                // Handle display of the new value
                onPreferenceChange(newPreference, editTextPreferenceString);
            }
            else {
                // Listen for changes on the Preference
                newPreference.setOnPreferenceChangeListener(this);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(newPreference.getContext());

                // Get the new value associated with the key
                String preferenceString = sharedPref.getString(newPreference.getKey(), " ");

                // Handle display of the new value
                onPreferenceChange(newPreference, preferenceString);
            }

        }



        /**
         * Handles what happens when a Preference has changed
         * @param preference The Preference that was clicked.
         * @param newValue The new value of the Preference.
         * @return True to update the state of the Preference with the new value
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            if (preference instanceof EditTextPreference){
                String newValueString = newValue.toString();

                // Sets the summary for the Preference
                preference.setSummary(newValueString);
            }
            else if (preference instanceof ListPreference){
                String newValueString = newValue.toString();

                // Cast the preference to a ListPreference
                ListPreference myListPreference = (ListPreference) preference;

                // Find the index of the value passed
                int prefIndex = myListPreference.findIndexOfValue(newValueString);

                // Check if the index is valid
                // Valid index are from 0 and above
                if (prefIndex >= 0){

                    // Get the Array of Labels of the List Preference
                    CharSequence[] labels = myListPreference.getEntries();

                    // Find the particular label from the CharSequence Array using the index
                    // and then displays the new label using setSummary
                    preference.setSummary(labels[prefIndex]);
                }
            }

            return true;
        }

    }


}
