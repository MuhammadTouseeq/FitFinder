package com.highbryds.fitfinder.callbacks

interface FTPCallback {
    fun isFTPUpload(isUploaded: Boolean, fileName: String)
}