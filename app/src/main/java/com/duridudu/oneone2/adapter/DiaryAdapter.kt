package com.duridudu.oneone2.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.duridudu.oneone2.databinding.ItemDiariesBinding
import com.duridudu.oneone2.model.Diary
import com.duridudu.oneone2.model.DiaryDao
import com.duridudu.oneone2.viewmodel.DiaryViewModel
import com.duridudu.oneone2.viewmodel.UserViewModel

class DiaryAdapter(private val onItemClick: (Diary) -> Unit,
                   private val onDeleteClickListener: DiaryDao)
    : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {
    private var diaries: MutableList<Diary> = mutableListOf() // 초기화된 빈 리스트로 시작
    private lateinit var viewModel: DiaryViewModel
    private lateinit var userViewModel : UserViewModel
    inner class DiaryViewHolder(
        itemView: View,
        private val binding: ItemDiariesBinding) :
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

            btnDelete.setOnClickListener {
                Log.d("ADAPTER++", "삭제 클릭")
                onDeleteClickListener.onDeleteClick(diary)

            }

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
        viewModel = ViewModelProvider(parent.context as FragmentActivity)[DiaryViewModel::class.java]
        userViewModel = ViewModelProvider(parent.context as FragmentActivity)[UserViewModel::class.java]
        val binding = ItemDiariesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(holder: DiaryAdapter.DiaryViewHolder, position: Int) {
        holder.onBind(diaries[position])
    }

    override fun getItemCount(): Int {
        return diaries.size
    }

    fun submitList(diaries: MutableList<Diary>) {
        this.diaries = diaries
        Log.d("ADOPTER++", diaries.size.toString())
        notifyDataSetChanged()
    }

    // 삭제된 아이템 제거
    fun removeItem(diary: Diary) {
        val index = diaries.indexOfFirst { it.diaryId == diary.diaryId } // diaryId를 기준으로 동등성 비교
        if (index != -1) {
            diaries.removeAt(index)
            notifyItemRemoved(index)
        }
    }


}
