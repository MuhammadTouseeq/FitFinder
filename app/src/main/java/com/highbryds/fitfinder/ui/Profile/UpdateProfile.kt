package com.highbryds.fitfinder.ui.Profile

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.gson.Gson
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.callbacks.FTPCallback
import com.highbryds.fitfinder.commonHelper.*
import com.highbryds.fitfinder.model.UsersData
import com.highbryds.fitfinder.utils.PathUtil
import com.highbryds.fitfinder.vm.Profile.UpdateProfileViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_update_profile.*
import kotlinx.android.synthetic.main.custom_dialog.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject


private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1

@AndroidEntryPoint
class UpdateProfile : AppCompatActivity(), ApiResponseCallBack, MultiplePermissionsListener,
    FTPCallback {

    @Inject
    lateinit var updateProfileViewModel: UpdateProfileViewModel
    var filePart: MultipartBody.Part? = null
    lateinit var model: RequestBody
    val items = listOf("Male", "Female")
    lateinit var filePath: String
    lateinit var usersData: UsersData
    var Tempphone = ""
    var isOTPVerifed = false
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        supportActionBar?.title = "Update Profile"
        toolbar.setNavigationOnClickListener {
            finish()
        }

        updateProfileViewModel.apiResponseCallBack = this

        val permissions = listOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val adapter = ArrayAdapter(this, R.layout.gender_list, items)
        AC_Gender.setAdapter(adapter)

        IV_bio.setOnClickListener {
            Dexter.withContext(this).withPermissions(permissions).withListener(this).check()
        }

        updateProfile.setOnClickListener {
            if (name.text.toString().isEmpty() || email.text.toString()
                    .isEmpty() || phone.text.toString().isEmpty() || age.text.toString()
                    .isEmpty() ||
                gender.editText!!.text.isEmpty() || city.text.toString()
                    .isEmpty() || country.text.toString().isEmpty() ||
                heading.text.toString().isEmpty() || description.text.toString()
                    .isEmpty() || phone.text.toString().length != 11
            ) {
                this.toast(this, "Please provide complete information.")
            } else {

                val ph = "+92" + phone.text.toString().substring(1);

                if (PrefsHelper.getBoolean(Constants.Pref_isOTPVerifed) && if (!PrefsHelper.getString(
                            Constants.Pref_isOTPMobile
                        ).contains("+92")
                    ) phone.text.toString()
                        .equals(PrefsHelper.getString(Constants.Pref_isOTPMobile)) else ph.equals(
                        PrefsHelper.getString(Constants.Pref_isOTPMobile)
                    )
                ) {
                    // can update profile
                    updateProfile()
                } else if (PrefsHelper.getBoolean(Constants.Pref_isOTPVerifed) && !ph.equals(
                        PrefsHelper.getString(Constants.Pref_isOTPMobile)
                    )
                ) {
                    otpVerifyPopup(ph)
                    // you have to verify again phone seems to be changes
                } else if (!PrefsHelper.getBoolean(Constants.Pref_isOTPVerifed)) {
                    otpVerifyPopup(ph)
                }

            }

        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.

            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                if (e is FirebaseAuthInvalidCredentialsException) {
                    this@UpdateProfile.toast(this@UpdateProfile, e.toString())
                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    this@UpdateProfile.toast(this@UpdateProfile, e.toString())
                    // The SMS quota for the project has been exceeded
                    // ...
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                this@UpdateProfile.toast(this@UpdateProfile, "Code sent on entered cell number.")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

            }
        }

        setProfileData()
    }

    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
            //IV_bio.setImageURI(data?.data)
            Glide
                .with(this)
                .load(data?.data)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(IV_bio);
            val uri = data?.data
            val file = File(PathUtil.getPath(this, uri))
            filePath = file.absolutePath
            val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
            filePart = MultipartBody.Part.createFormData("ProfilePic", file.name, requestBody)
            model = RequestBody.create(
                "text/plain".toMediaTypeOrNull(),
                KotlinHelper.getUsersData().SocialId
            )
        }
    }

    override fun getError(error: String) {
        Log.d("UPLOADPIC", error)
        this.toast(this, error)
    }

    override fun getSuccess(success: String) {
        this.toast(this, success)
        PrefsHelper.getString(Constants.Pref_UserData)
        loadingView.visibility = View.GONE
        finish()
    }

    private fun setProfileData() {
        val gson = Gson()
        val usersData = PrefsHelper.getString(Constants.Pref_UserData)
        val ud: UsersData = gson.fromJson(usersData, UsersData::class.java)

        name.setText(ud.name)
        email.setText(ud.emailAdd)
        age.setText(ud.age.toString())
        val pos = if (ud.Gender.equals("Male", ignoreCase = true)) 0 else 1
        AC_Gender.setText(ud.Gender, false)
        city.setText(ud.City)
        country.setText(ud.Country)
        heading.setText(ud.headline)
        description.setText(ud.About)
        phone.setText(ud.cellNumber.replace("+92", "0"))

        Glide
            .with(this)
            .load(ud.imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(IV_bio);
    }

    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
        selectImageInAlbum()
    }

    override fun onPermissionRationaleShouldBeShown(
        p0: MutableList<PermissionRequest>?,
        p1: PermissionToken?
    ) {
        this.toast(this, "Permission is required to load images")
    }

    override fun isFTPUpload(isUploaded: Boolean, fileName: String) {
        usersData.imageUrl = "http://highbryds.com/fitfinder/stories/" + fileName
        updateProfileViewModel.uploadUsersData(usersData)
    }


    fun otpVerifyPopup(phone: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_dialog)

        val dialogResend: Button = dialog.findViewById(R.id.resend) as Button
        val dialogVerify: Button = dialog.findViewById(R.id.verify) as Button
        val dialogotpVerify: TextInputEditText = dialog.findViewById(R.id.otp) as TextInputEditText


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks
        ) // OnVerificationStateChangedCallbacks

        dialogVerify.setOnClickListener {
            if (!dialogotpVerify.text.toString().isEmpty()) {
                verifyPhoneNumberWithCode(
                    storedVerificationId,
                    dialogotpVerify.text.toString(),
                    phone
                )
            } else {
                this@UpdateProfile.toast(this@UpdateProfile, "Enter OTP Code")
            }
        }

        // dialog.setCancelable(false)
        dialog.show()
    }


    fun verifyPhoneNumberWithCode(verificationId: String?, code: String, phone: String) {
        if (verificationId !== "" || verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(
                verificationId!!,
                code
            )
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, object : OnCompleteListener<AuthResult?> {
                    override fun onComplete(@NonNull task: Task<AuthResult?>) {
                        if (task.isSuccessful()) {
                            this@UpdateProfile.toast(this@UpdateProfile, "Code verified")
                            PrefsHelper.putString(Constants.Pref_isOTPMobile, phone)
                            PrefsHelper.putBoolean(Constants.Pref_isOTPVerifed, true)
                            updateProfile()
                        } else {
                            if (task.getException() is FirebaseAuthInvalidCredentialsException) {
                            }
                            this@UpdateProfile.toast(this@UpdateProfile, "Error occurred")
                        }
                    }
                })
        }
    }

    private fun updateProfile() {
        usersData = UsersData(
            heading.text.toString().trim(),
            description.text.toString().trim(),
            name.text.toString().trim(),
            PrefsHelper.getString(
                Constants.Pref_DeviceToken,
                ""
            ).trim(),
            KotlinHelper.getUsersData().SocialId.trim(),
            KotlinHelper.getUsersData().SocialType.trim(),
            email.text.toString().trim(),
            phone.text.toString().trim(),
            KotlinHelper.getUsersData().imageUrl.trim(),
            Integer.parseInt(
                age.text.toString().trim()
            ),
            gender.editText!!.text.toString().trim(),
            city.text.toString().trim(),
            country.text.toString().trim()
        )
        if (filePart != null) {
            // updateProfileViewModel.uploadProfile(filePart!!, model , usersData)
            val filename: String = filePath.substring(filePath.lastIndexOf("/") + 1)
            val ftpHelper: FTPHelper = FTPHelper()
            ftpHelper.init(this)
            ftpHelper.AsyncTaskExample().execute(filePath, filename)
            PrefsHelper.putBoolean(Constants.Pref_IsProfileUpdate, true)
        } else {
            loadingView.visibility = View.VISIBLE
            updateProfileViewModel.uploadUsersData(usersData)
            PrefsHelper.putBoolean(Constants.Pref_IsProfileUpdate, true)
        }
    }


}