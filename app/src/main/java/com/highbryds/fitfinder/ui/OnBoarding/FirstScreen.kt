package com.highbryds.fitfinder.ui.OnBoarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.adapter.PagerAdapter
import kotlinx.android.synthetic.main.activity_first_screen.*


class FirstScreen : AppCompatActivity() {

    private val fragments = arrayOf(Onboarding2(), Onboarding3(), Onboarding4())
    private val fragmentTitles = arrayOf("", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_screen)

        setUpComponents()



    }

    private fun setUpComponents() {
//        fragments[0].arguments = bn
//        fragments[1].arguments = bn
//        tlFitnessTabs.setupWithViewPager(vpFitnessPager)
        viewpager.adapter = PagerAdapter(supportFragmentManager, fragments, fragmentTitles)
    }

}