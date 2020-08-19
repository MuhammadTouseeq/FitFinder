package com.android.indigo.ui.Main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.indigo.R
import com.android.indigo.callbacks.ApiErrorsCallBack
import com.android.indigo.commonHelper.toast
import com.android.indigo.vm.AuthViewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() , ApiErrorsCallBack {

    @Inject
    lateinit var userViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        userViewModel.apiErrorsCallBack = this
//        userViewModel.userdata?.observe(this, Observer {
//            for (item: UsersData in it) {
//                Log.d("DATAA", item.login)
//                Log.d("DATAA", item.id.toString())
//            }
//        })

        //JavaHelper.printHashKey(this)
    }



    override fun getError(error: String) {
        this.toast(this, error)
    }
}
