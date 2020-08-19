package com.android.indigo.room.tables

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Users (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var login: String)

