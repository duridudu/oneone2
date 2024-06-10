package com.duridudu.oneone2.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    // 편의 메소드
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dto: User)

    // 쿼리 메소드
    @Query("select * from userTable")
    fun getUser(): User

    @Query("select * from userTable where uid = (:id)")
    fun selectOne(id:String): User

    @Update
    suspend fun update(dto: User)

    @Delete
    fun delete(dto: User)
}