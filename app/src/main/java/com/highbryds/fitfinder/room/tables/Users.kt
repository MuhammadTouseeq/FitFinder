package com.highbryds.fitfinder.room.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import javax.inject.Inject


@Entity
data class Users (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var login: String)

