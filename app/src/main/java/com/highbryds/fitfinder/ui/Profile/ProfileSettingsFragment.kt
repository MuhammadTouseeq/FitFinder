package com.highbryds.fitfinder.ui.Profile

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.highbryds.fitfinder.R

class ProfileSettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }




}