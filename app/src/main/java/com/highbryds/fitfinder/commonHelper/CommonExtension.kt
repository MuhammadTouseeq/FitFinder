package com.highbryds.fitfinder.commonHelper

import android.content.Context
import android.widget.EditText
import android.widget.Toast

fun Context.toast(context: Context,  msg: String): Toast{
    return Toast.makeText(context, msg, Toast.LENGTH_SHORT).apply { show() }
}
