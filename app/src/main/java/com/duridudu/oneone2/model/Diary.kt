package com.duridudu.oneone2.model

import android.os.Parcel
import android.os.Parcelable

data class Diary(
    val diaryId: String? = null, // 파이어베이스에서 생성된 고유 키
    val title: String? = null,
    val content: String? = null,
    val timestamp: String? = null,
    val photoUrl: String? = null
)