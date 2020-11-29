package com.kraaiennest.kraaiennestapp.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import com.kraaiennest.kraaiennestapp.R;

import java.util.Map;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
