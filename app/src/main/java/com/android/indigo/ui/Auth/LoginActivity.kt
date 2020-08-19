package com.android.indigo.ui.Auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.indigo.R
import com.android.indigo.callbacks.ApiErrorsCallBack
import com.android.indigo.vm.AuthViewModels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity(), ApiErrorsCallBack {


    @Inject
    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel.apiErrorsCallBack = this


//        loginViewModel.hitApi()
//        loginViewModel.userdata?.observe(this@LoginActivity, Observer {
//            Log.d("FBDATA", it.toString())
//            //this@LoginActivity.toast(this@LoginActivity , it.toString())
//
//
//        });
    }

    override fun getError(error: String) {
        TODO("Not yet implemented")
    }
}