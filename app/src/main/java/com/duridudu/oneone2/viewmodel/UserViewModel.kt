package com.duridudu.oneone2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duridudu.oneone2.model.User
import com.duridudu.oneone2.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel:ViewModel() {
   /// val nowUser: User
    private val userRepository:UserRepository = UserRepository.get()
    init {
        //nowUser = userRepository.getUser()
    }

    fun getOne(id : String) = userRepository.getOne(id)
    fun insert(dto: User) {
        userRepository.insert(dto)
    }
    fun update(dto:User) = viewModelScope.launch(Dispatchers.IO){
        userRepository.update(dto)
    }
    fun delete(dto:User)=viewModelScope.launch(Dispatchers.IO){
        userRepository.delete(dto)
    }
}