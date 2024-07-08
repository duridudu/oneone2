package com.duridudu.oneone2.repository

import android.util.Log
import com.duridudu.oneone2.model.Diary
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DiaryRepository {
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance("https://oneone2-4660f-default-rtdb.asia-southeast1.firebasedatabase.app")
    private lateinit var diaryRef: DatabaseReference
    suspend fun getDiaries(userId: String): MutableList<Diary> {
        diaryRef = database.getReference("users/$userId/diaries")
        var diariesList = mutableListOf<Diary>()
        return suspendCoroutine { continuation ->
            diaryRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val diary = childSnapshot.getValue(Diary::class.java)
                        diary?.let { diariesList.add(it) }
                    }
                    continuation.resume(diariesList)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }



}