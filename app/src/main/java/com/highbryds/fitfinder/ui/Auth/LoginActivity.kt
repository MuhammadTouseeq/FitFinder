package com.highbryds.fitfinder.ui.Auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.commonHelper.Constants
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.commonHelper.PrefsHelper
import com.highbryds.fitfinder.commonHelper.toast
import com.highbryds.fitfinder.model.UserAgent
import com.highbryds.fitfinder.model.UsersData
import com.highbryds.fitfinder.ui.Main.HomeMapActivity
import com.highbryds.fitfinder.ui.Profile.UserProfileMain
import com.highbryds.fitfinder.vm.AuthViewModels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject


@AndroidEntryPoint
@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity(), ApiResponseCallBack {

    lateinit var callbackManager: CallbackManager
    private val EMAIL = "email"

    lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        loginViewModel.apiResponseCallBack = this

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)



        login_button.setOnClickListener{
            login_button.setReadPermissions(listOf(EMAIL))
           /// login_button.setReadPermissions(listOf("user_status"))
            callbackManager = CallbackManager.Factory.create()
            LoginManager.getInstance().registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult?) {
                        val graphRequest =
                            GraphRequest.newMeRequest(result?.accessToken) { obj, response ->
                                try {
                                    if (obj.has("id")) {
                                        //Log.d("FBDATA" , obj.getString("name"))
                                        //Log.d("FBDATA" , obj.getString("email"))
                                        val usersData = UsersData("" , "",
                                            KotlinHelper.isJsonObjNull(
                                                obj,
                                                "name"
                                            ),
                                            PrefsHelper.getString(Constants.Pref_DeviceToken, ""),
                                            obj.getString("id"),
                                            "FB",
                                            obj.getString("email"),
                                            obj.getJSONObject(
                                                "picture"
                                            ).getJSONObject("data").getString("url"),
                                            0,
                                            KotlinHelper.isJsonObjNull(
                                                obj, "gender"
                                            ),
                                            "",
                                            ""
                                        )
                                        userLoginRequest(usersData)
                                    }

                                } catch (e: Exception) {
                                    this@LoginActivity.toast(this@LoginActivity, e.toString())
                                }
                            }

                        val param = Bundle()
                        param.putString(
                            "fields",
                            "name,email,id,gender,birthday,picture.type(large)"
                        )
                        graphRequest.parameters = param
                        graphRequest.executeAsync()
                    }

                    override fun onCancel() {
                        Log.d("FBDATA", "Cancel")
                    }

                    override fun onError(error: FacebookException?) {
                        Log.d("FBDATA", error.toString())
                    }


                })
        }

        btnFB.setOnClickListener {
            login_button.performClick()
        }

        btnGoogle.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, 1)
        }

        loginViewModel.userdata?.observe(this@LoginActivity, Observer {
            loadingProgress.visibility = View.GONE
            val gson = Gson()
            val json = gson.toJson(it)
            PrefsHelper.putString(Constants.Pref_UserData, json)
            PrefsHelper.putBoolean(Constants.Pref_IsLogin, true)
            Log.d("USERDATA", it.toString())

            val intent = Intent(this, HomeMapActivity::class.java)
            startActivity(intent)
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignIn(task);
        } else {
            callbackManager?.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun handleGoogleSignIn(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                val personName: String? = account.getDisplayName()
                val personGivenName: String? = account.getGivenName()
                val personFamilyName: String? = account.getFamilyName()
                val personEmail: String? = account.getEmail()
                val personId: String? = account.getId()
                val personPhoto: Uri? = account.getPhotoUrl()
                val usersData = UsersData(
                    personName!!, PrefsHelper.getString(
                        Constants.Pref_DeviceToken,
                        ""
                    ),
                    personId!!,
                    "Google",
                    personEmail!!,
                    personPhoto.toString()
                )
                userLoginRequest(usersData)
            }
        } catch (e: Exception) {
            this.toast(this, "Something went wrong")
        }


    }

    fun userLoginRequest(usersData: UsersData){
        loadingProgress.visibility = View.VISIBLE
        val userAgent = UserAgent(
            KotlinHelper.getUniqueID(),
            usersData.SocialId,
            KotlinHelper.getDeviceMake(),
            KotlinHelper.getDeviceModel(),
            "Android",
            KotlinHelper.getAndroidVersion()
        )
        loginViewModel.loginUser(usersData, userAgent)
    }

    override fun getError(error: String) {
        googleSignInClient.signOut()
        loadingProgress.visibility = View.GONE
        this.toast(this, error)
    }

    override fun getSuccess(success: String) {
        loadingProgress.visibility = View.GONE
        this.toast(this, success)
    }
}
