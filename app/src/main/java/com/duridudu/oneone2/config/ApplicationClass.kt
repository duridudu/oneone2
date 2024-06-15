package com.duridudu.oneone2.config

import android.app.Application
import android.util.Log
import com.duridudu.oneone2.repository.UserRepository
import com.google.firebase.FirebaseApp

class ApplicationClass : Application() {
    override fun onCreate(){
        super.onCreate()
        Log.d("TEST++", "ApplicationClass")
        FirebaseApp.initializeApp(this)
       UserRepository.initialize(this)
    }
}