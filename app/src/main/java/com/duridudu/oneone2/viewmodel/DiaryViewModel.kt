package com.duridudu.oneone2.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.duridudu.oneone2.model.Diary
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DiaryViewModel:ViewModel() {
    private var selectedDiary: Diary? = null

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

}