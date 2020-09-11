package com.highbryds.fitfinder.ui.Profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.highbryds.fitfinder.R
import com.highbryds.fitfinder.callbacks.ApiResponseCallBack
import com.highbryds.fitfinder.commonHelper.Constants
import com.highbryds.fitfinder.commonHelper.KotlinHelper
import com.highbryds.fitfinder.commonHelper.PrefsHelper
import com.highbryds.fitfinder.commonHelper.toast
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
import kotlinx.android.synthetic.main.activity_update_profile.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.jar.Manifest
import javax.inject.Inject

private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1

@AndroidEntryPoint
class UpdateProfile : AppCompatActivity(), ApiResponseCallBack, MultiplePermissionsListener {

    @Inject
    lateinit var updateProfileViewModel: UpdateProfileViewModel
    var filePart: MultipartBody.Part? = null
    lateinit var model: RequestBody
    val items = listOf("Male", "Female")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

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
            if(name.text.toString().isEmpty() || email.text.toString().isEmpty() || age.text.toString().isEmpty() || gender.editText!!.text.isEmpty() || city.text.toString().isEmpty() || country.text.toString().isEmpty()){
                    this.toast(this , "Please provide complete data")
            }else{
                val usersData = UsersData(name.text.toString() , PrefsHelper.getString(Constants.Pref_DeviceToken , "") , KotlinHelper.getUsersData().SocialId , KotlinHelper.getUsersData().SocialType, email.text.toString()  , KotlinHelper.getUsersData().imageUrl,Integer.parseInt(age.text.toString()), gender.editText!!.text.toString() , city.text.toString() , country.text.toString())
                if(filePart != null){
                    updateProfileViewModel.uploadProfile(filePart!!, model , usersData)
                }else{
                    updateProfileViewModel.uploadUsersData(usersData)
                }

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
            val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
            filePart = MultipartBody.Part.createFormData("ProfilePic", file.name, requestBody)
            model = RequestBody.create("text/plain".toMediaTypeOrNull(), KotlinHelper.getUsersData().SocialId)
        }
    }

    override fun getError(error: String) {
        Log.d("UPLOADPIC", error)
        this.toast(this , error)
    }

    override fun getSuccess(success: String) {
        this.toast(this , success)
        PrefsHelper.getString(Constants.Pref_UserData)
        finish()
    }

    private fun setProfileData(){
        val gson = Gson()
        val usersData = PrefsHelper.getString(Constants.Pref_UserData)
        val ud: UsersData = gson.fromJson(usersData, UsersData::class.java)

        name.setText(ud.name)
        email.setText(ud.emailAdd)
        age.setText(ud.age.toString())
        val pos = if (ud.Gender.equals("Male" , ignoreCase = true)) 0 else 1
        AC_Gender.setText(ud.Gender , false)
        city.setText(ud.City)
        country.setText(ud.Country)

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
        this.toast(this , "Permission is required to load images")
    }

}