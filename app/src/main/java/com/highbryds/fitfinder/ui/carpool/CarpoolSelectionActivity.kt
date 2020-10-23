package com.highbryds.fitfinder.ui.carpool

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.ui.carpool.fitrider.FR_RequestForm
import kotlinx.android.synthetic.main.activity_user_selection_type.*

class CarpoolSelectionActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_selection_type)


        FR_Option.setOnClickListener {
            val intent = Intent(this, FR_RequestForm::class.java)
            startActivity(intent)
        }
    }
}