package com.duridudu.oneone2.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.duridudu.oneone2.model.Diary
import com.duridudu.oneone2.model.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DiaryViewModel:ViewModel() {
    private var selectedDiary: Diary? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var diaryRef: DatabaseReference
    private lateinit var userViewModel: UserViewModel

    // LiveData를 통해 삭제 작업의 성공 여부를 알림
    private val _deleteResult = MutableLiveData<Boolean>()
    private val _deleteDiary = MutableLiveData<Diary>()
    val deleteResult: LiveData<Boolean>
        get() = _deleteResult
    val deleteDiary: LiveData<Diary>
        get() = _deleteDiary


    fun setSelectedDiary(diary: Diary) {
        selectedDiary = diary
    }

    fun getSelectedDiary(): Diary? {
        return selectedDiary
    }

    // 초기화 메서드
    fun initSelectedDiary() {
        selectedDiary = null
    }

    // Firebase에서 가져온 "yyyy-dd-mm hh:mm" 형식의 문자열을 LocalDateTime으로 변환하는 함수
    @RequiresApi(Build.VERSION_CODES.O)
    fun parseFirebaseDateTime(dateTimeString: String): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm")
        return LocalDateTime.parse(dateTimeString, formatter)
    }

    suspend fun deleteDiary(diary: Diary, uid: String){

        initFirebase(uid)
        Log.d("VIEWMODEL++", "deleteDiary")
        diaryRef = diary.diaryId?.let { diaryRef.child(it) }!!

        diaryRef.removeValue()
            .addOnSuccessListener {
//                // 삭제 리스너
//                StyleableToast.makeText(  , "삭제를 클릭했다.", R.style.myToast).show()
                //==> viewmodel에서 처리하지말라함
                // 삭제 성공 시 LiveData를 통해 결과를 전달
                //_deleteResult.value = true
                _deleteDiary.value = diary
            }
            .addOnFailureListener { exception ->
                // 삭제 실패 처리
                // 삭제 실패 시 LiveData를 통해 결과를 전달
                //_deleteResult.value = false

            }

    }
    private suspend fun initFirebase(uid: String) {

        try {
            Log.d("VIEWMODEL++", "INIT FIREBASE")

            Log.d("VIEWMODEL++", uid)
            // Firebase Database 인스턴스 초기화
            database =
                FirebaseDatabase.getInstance("https://oneone2-4660f-default-rtdb.asia-southeast1.firebasedatabase.app")
            diaryRef = database.getReference("users/$uid/diaries")
            Log.d("VIEWMODEL++", "IN initFirebase")

            // 데이터 요청
            //  getFBContentData()
            Log.d("VIEWMODEL++", "after getFBContentData")


        } catch (e: Exception) {
            Log.e("VIEWMODEL++", "Firebase initialization failed", e)
        }
    }


}