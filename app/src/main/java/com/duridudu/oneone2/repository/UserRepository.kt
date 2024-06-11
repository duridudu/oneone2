package com.duridudu.oneone2.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.duridudu.oneone2.model.User
import com.duridudu.oneone2.model.UserDatabase

private const val DATABASE_NAME = "user-database.db"
class UserRepository private constructor(context: Context){
    private val database: UserDatabase = Room.databaseBuilder(
        context.applicationContext,
        UserDatabase::class.java,
        DATABASE_NAME
    ).build()

    // Dao 객체
    private val userDao = database.userDao()

    // 서비스단에서 사용할 함수(구 메소드 현 함수)
    fun getUser(): User = userDao.getUser()

    fun getOne(id:String):User = userDao.selectOne(id)

    fun insert(dto: User) = userDao.insert(dto)

    suspend fun update(dto : User) = userDao.update(dto)

    fun delete(dto:User) = userDao.delete(dto)

    // 초기화 및 생성
    companion object{
        private var INSTANCE: UserRepository?=null

        fun initialize(context: Context){
            Log.d("REPOSITORY++", "initialize")
            if (INSTANCE==null){
                Log.d("REPOSITORY++2", "INSTANCE==null")
                INSTANCE = UserRepository(context)
            }
        }

        fun get():UserRepository{
            return INSTANCE ?:
            throw IllegalStateException("UserRepository must be initialized!")
        }
    }
}

