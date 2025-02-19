package com.example.gamescore.fragments.main;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.gamescore.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}