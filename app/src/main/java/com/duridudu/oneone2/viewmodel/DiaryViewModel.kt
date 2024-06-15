package com.duridudu.oneone2.viewmodel

import androidx.lifecycle.ViewModel
import com.duridudu.oneone2.model.Diary

class DiaryViewModel:ViewModel() {
    private var selectedDiary: Diary? = null

    fun setSelectedDiary(diary: Diary) {
        selectedDiary = diary
    }

    fun getSelectedDiary(): Diary? {
        return selectedDiary
    }
}