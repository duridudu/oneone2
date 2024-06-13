package com.duridudu.oneone2.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duridudu.oneone2.model.User
import com.duridudu.oneone2.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel:ViewModel() {
   // var user = User()
    private val userRepository:UserRepository = UserRepository.get()

    suspend fun getUser(): User{
        return withContext(Dispatchers.IO){
            userRepository.getUser()
        }
    }


    fun getOne(id : String) = userRepository.getOne(id)
    fun insert(dto: User) = viewModelScope.launch(Dispatchers.IO){
        Log.d("USER++333", dto.uid+dto.name+dto.email+dto.profileurl)
        userRepository.insert(dto)
    }
//    suspend fun update(dto:User) {
//        userRepository.update(dto)
//    }
    fun delete(dto:User){
        userRepository.delete(dto)
    }
}