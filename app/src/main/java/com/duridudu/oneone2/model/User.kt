package com.duridudu.oneone2.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

    @Entity(tableName="userTable")
    class User (
        @ColumnInfo(name="uid") @PrimaryKey(autoGenerate = true) var uid:String,
        @ColumnInfo(name="name") val name:String = "",
        @ColumnInfo(name="email") val email:String = "",
        @ColumnInfo(name="profileurl") val profileurl:String = "",
    ): Serializable {

    }


