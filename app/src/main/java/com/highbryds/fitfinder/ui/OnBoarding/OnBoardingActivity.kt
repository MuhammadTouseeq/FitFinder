package com.highbryds.fitfinder.ui.OnBoarding

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.github.appintro.AppIntroPageTransformerType
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.ui.Auth.LoginActivity


class OnBoardingActivity : AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setNextArrowColor(R.color.colorPrimary)
        setSkipArrowColor(R.color.colorPrimary)

        isColorTransitionsEnabled=true
        addSlide(AppIntroFragment.newInstance(
            title=" Near by Stories !",
            description = "Fitfinder for you ,share your story anytime from anywhere.",
            imageDrawable = R.drawable.onboard1,
                    titleColor = Color.BLACK,
            descriptionColor = Color.GRAY
        ))

                addSlide(AppIntroFragment.newInstance(
            title="Car pooling",
            description = "Need to book ride in low cost or want to become fitDriver ? FitFinder for you",
            imageDrawable = R.drawable.onboard2,
                    titleColor = Color.BLACK,
                    descriptionColor = Color.GRAY
        ))

        addSlide(AppIntroFragment.newInstance(
            title="Need Urgent Help ?",
            description = "Nearby people will reach out you and help you in case of emergency .",
            imageDrawable = R.drawable.onboard4,
            titleColor = Color.BLACK,
            descriptionColor = Color.GRAY
        ))

        addSlide(AppIntroFragment.newInstance(
            title="Urgent need of blood ?",
            description = "People will be notified , blood donor will contact you through this app .",
            imageDrawable = R.drawable.blood_donor,
            titleColor = Color.BLACK,
            descriptionColor = Color.GRAY
        ))

        setTransformer(AppIntroPageTransformerType.Depth)
// Change Indicator Color
        setIndicatorColor(
            selectedIndicatorColor = resources.getColor(R.color.colorPrimary),
            unselectedIndicatorColor = resources.getColor(R.color.colorPrimaryLight)
        )

        // Ask Location Permission
// permission on slide 2
        askForPermissions(
            permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION

            ),
            slideNumber = 2,
            required = true)

        isColorTransitionsEnabled = true

    }


    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)

        finish()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}