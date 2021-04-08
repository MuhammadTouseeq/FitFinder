package com.highbryds.fitfinder.ui.Profile

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.highbryds.fitfinder.BuildConfig
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.model.UserProfileVisibility
import com.highbryds.fitfinder.model.carpool.Feedback
import com.highbryds.fitfinder.vm.Profile.ProfileSettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileSettingsFragment() : PreferenceFragmentCompat(), ApiResponseCallBack,
    SharedPreferences.OnSharedPreferenceChangeListener {
    lateinit var data: EditTextPreference
    lateinit var profileSwitch : SwitchPreference
    var isProfile = true
    val ProfileSettingsViewModel: ProfileSettingsViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        setUpDefaultValues()
    }

    fun setUpDefaultValues() {
        findPreference<Preference>(resources.getString(R.string.app_version))?.setSummary(
            BuildConfig.VERSION_NAME
        )


    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {

            "key_user_app_feedback" -> {
                data = findPreference(resources.getString(R.string.key_user_app_feedback))!!
                if (!data?.text.equals("")) {
                    ProfileSettingsViewModel.uploadData(
                        Feedback(
                            KotlinHelper.getSocialID(),
                            KotlinHelper.getUsersData().cellNumber,
                            data?.text!!
                        ), this
                    )

                }

            }

            "key_profile_view" -> {
                isProfile = sharedPreferences.getBoolean("key_profile_view", true)
                val isProfileString = if (isProfile) "Public" else "Private"
                val userProfileVisibility =
                    UserProfileVisibility(KotlinHelper.getSocialID(), isProfileString)
                ProfileSettingsViewModel.uploadProfilePref(userProfileVisibility, this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        if (KotlinHelper.getUsersData().profileType.equals("Private", true)) {
            profileSwitch =  findPreference("key_profile_view")!!
            profileSwitch.isChecked = false
        }
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun getError(error: String) {
        activity?.toast(requireActivity(), "Unable to submit your Feedback at this time.")

    }

    override fun getSuccess(success: String) {
        if (success.equals("1")) {

        } else {
            activity?.toast(requireActivity(), "Thanks for your valuable Feedback.")
            data?.text = ""
        }

    }
}