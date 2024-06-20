package com.duridudu.oneone2.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duridudu.oneone2.model.User
import com.duridudu.oneone2.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel:ViewModel() {
   // var user = User() val user: LiveData<User> = _user
   //
   //    fun setUser(user: User) {
   //        _user.value = user
    private val userRepository:UserRepository = UserRepository.get()
    private var _user: MutableLiveData<User> = MutableLiveData()
    //val user: LiveData<User> = _user
    val user: LiveData<User>
        get() = _user

    fun setUser(user: User) {
        _user.value = user
        Log.d("VIEW@E++", _user.value!!.uid)
    }


    suspend fun getUser(): User {
        Log.d("USERVIEWMODEL++", "뷰모델")
       // return userRepository.getUser()
        return withContext(Dispatchers.IO) {
            val user:User = userRepository.getUser()
            Log.d("USERVIEWMODEL++2", "사용자 UID: ${user.uid} + ${user.name}")
            user // 사용자 정보 반환
        }
    }

    // 코루틴을 사용하여 사용자 정보 가져오기
    fun getUser2(): LiveData<User> {
        val userLiveData = MutableLiveData<User>()

        viewModelScope.launch {
            try {
                val user = withContext(Dispatchers.IO) {
                    userRepository.getUser() // 예시: Repository에서 사용자 정보 가져오기
                }
                userLiveData.postValue(user) // LiveData를 통해 UI에 사용자 정보 전달
            } catch (e: Exception) {
                // 예외 처리
                Log.e("UserViewModel", "Error fetching user: ${e.message}", e)
            }
        }

        return userLiveData
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