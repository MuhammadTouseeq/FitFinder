package com.highbryds.fitfinder.ui.OnBoarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.ui.Auth.LoginActivity
import kotlinx.android.synthetic.main.activity_third_screen.*

class Onboarding4 : Fragment(R.layout.activity_third_screen) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_login.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}