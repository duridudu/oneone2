package com.duridudu.oneone2.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.duridudu.oneone2.databinding.ItemDiariesBinding
import com.duridudu.oneone2.model.Diary
import com.google.firebase.database.FirebaseDatabase

class DiaryAdapter(private val onItemClick: (Diary) -> Unit) : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {
    private var diaries: List<Diary> = listOf() // 초기화된 빈 리스트로 시작

    inner class DiaryViewHolder(itemView: View, private val binding: ItemDiariesBinding) :
        RecyclerView.ViewHolder(itemView) {
        var title = binding.listTitle
        var timestamp = binding.tvTimeStamp
        var btnDelete = binding.listDelete
        // 인터페이스 정의

        fun onBind(diary: Diary) {
            //Log.d("ADAPTER++", "onBind")

            title.text = diary.title

            timestamp.text = diary.timestamp
            (timestamp.text as String?)?.let { Log.d("ADAPTER++", it) }
            // 삭제 버튼 클릭 리스너 설정
//            btnDelete.setOnClickListener {
//                OnDeleteClickListener.onDeleteClick(diary)
//            }
//            btnDelete.setOnClickListener {
//
//                // 삭제 리스너
//
//            }

            itemView.setOnClickListener {
                // 클릭 시 WriteFragment로
                onItemClick(diary)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DiaryAdapter.DiaryViewHolder {
        Log.d("ADAPTER++", "onCreateViewHolder")
        val binding = ItemDiariesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(holder: DiaryAdapter.DiaryViewHolder, position: Int) {
        holder.onBind(diaries[position])
    }

    override fun getItemCount(): Int {
        return diaries.size
    }

    fun submitList(diaries: List<Diary>) {
        this.diaries = diaries
        Log.d("ADOPTER++", diaries.size.toString())
        notifyDataSetChanged()
    }
    interface OnDeleteClickListener {
        fun onDeleteClick(diary: Diary)
    }


}
