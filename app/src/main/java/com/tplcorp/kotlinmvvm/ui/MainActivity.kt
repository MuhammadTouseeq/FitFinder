package com.tplcorp.kotlinmvvm.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.tplcorp.kotlinmvvm.R
import com.tplcorp.kotlinmvvm.model.UsersData
import com.tplcorp.kotlinmvvm.vm.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userViewModel.userdata?.observe(this, Observer {
            for (item: UsersData in it) {
                Log.d("DATAA", item.login)
                Log.d("DATAA", item.id.toString())
            }
        })
    }
}
