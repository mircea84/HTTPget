package com.example.HTTPget;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

//TODO setarile nu se updateaza automat!!!!
//TODO de adaugat si celelalte servicii de cloud xively thinkgeek devicehub
public class SettingsActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {


    public static final String KEY_LIMIT = "pref_limit_key";
    public static final String KEY_PROJECTID = "pref_projectID_key";
    public static final String KEY_SENSORID = "pref_sensorID_key";
    public static final String KEY_API_KEY = "pref_api_key_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

//he instanceof operator compares an object to a specified type. You can use it to test if an object is an instance
// of a class, an instance of a subclass, or an instance of a class that implements a particular interface.
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        //if (pref instanceof EditTextPreference) {
            //EditTextPreference textPref = (EditTextPreference) pref;
            pref.setSummary(sharedPreferences.getString(key, ""));
        //}
    }

}