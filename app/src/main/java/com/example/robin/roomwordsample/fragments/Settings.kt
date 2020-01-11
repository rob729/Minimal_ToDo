package com.example.robin.roomwordsample.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.example.robin.roomwordsample.Activity.MainActivity
import com.example.robin.roomwordsample.Activity.splashScreen
import com.example.robin.roomwordsample.R

class Settings : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    var pref: SwitchPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_todo)
        val pmanager = preferenceManager
        pref = findPreference("nightMode")

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onStop() {
        super.onStop()
        // unregister the preference change listener
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onStart() {
        super.onStart()
        // register the preference change listener
        preferenceScreen.sharedPreferences
            .registerOnSharedPreferenceChangeListener(this)
    }


}