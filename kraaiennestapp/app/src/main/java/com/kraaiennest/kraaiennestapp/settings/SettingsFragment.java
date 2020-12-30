package com.kraaiennest.kraaiennestapp.settings;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import com.kraaiennest.kraaiennestapp.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
