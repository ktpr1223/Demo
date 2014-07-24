package com.example.project;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.example.project.R;


public class SettingsActivity2 extends PreferenceActivity
        implements
            OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("ê›íË");
    
        addPreferencesFromResource(R.xml.preferences2);
    }

    @Override
    protected void onResume() {
        super.onResume();

   
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

   
        getPreferenceScreen()
                .getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        
    }
}
