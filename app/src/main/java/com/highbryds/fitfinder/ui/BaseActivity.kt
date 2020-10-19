package com.highbryds.fitfinder.ui

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

open class BaseActivity: AppCompatActivity() {


    protected open fun bindToolbar(
        toolbar: Toolbar,
        titleName: String?
    ) {
        setSupportActionBar(toolbar)
       toolbar.setTitle(titleName)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            onBackPressed() }
    }
}