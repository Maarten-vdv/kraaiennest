package com.kraaiennest.opvang.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.kraaiennest.opvang.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
