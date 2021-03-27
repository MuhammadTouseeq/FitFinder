package com.highbryds.fitfinder.ui.Profile


import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.commonHelper.toast


class ProfileSettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)


        val myPref: Preference? = findPreference(getString(R.string.key_gallery_name))
        myPref?.setTitle("dasda")
//        myPref!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
//            sendFeedback(activity?.applicationContext)
//            true
//        }
//

//        myPref.onPreferenceChangeListener =
//            Preference.OnPreferenceChangeListener(object Preference.OnPreferenceChangeListener {
//
//            })

    }

//    private fun sendFeedback(context: Context?){}


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        //val pref: Preference? = findPreference(key)
        when (key) {
            "key_gallery_name" -> {
                activity?.toast(requireActivity(), "dasds")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}