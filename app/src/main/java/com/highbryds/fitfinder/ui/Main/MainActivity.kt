package com.highbryds.fitfinder.ui.Main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.callbacks.ApiErrorsCallBack
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.model.UsersData
import com.highbryds.fitfinder.vm.AuthViewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() , ApiErrorsCallBack {

    @Inject
    lateinit var userViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userViewModel.apiErrorsCallBack = this
        userViewModel.userdata?.observe(this, Observer {
            for (item: UsersData in it) {
                Log.d("DATAA", item.login)
                Log.d("DATAA", item.id.toString())
            }
        })
    }

    override fun getError(error: String) {
        this.toast(this, error)
    }
}
