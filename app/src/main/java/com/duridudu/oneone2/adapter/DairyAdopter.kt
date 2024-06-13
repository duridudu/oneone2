package com.duridudu.oneone2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.duridudu.oneone2.Lists
import com.duridudu.oneone2.R
import com.duridudu.oneone2.databinding.ItemDairiesBinding

class DairyAdopter(val context: Lists): RecyclerView.Adapter<DairyAdopter.DairyViewHolder>() {
    private lateinit var binding: ItemDairiesBinding
    inner class DairyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var title = binding.listTitle
        var timestamp = binding.tvTimeStamp
        var btnDelete = binding.listDelete

        fun onBind(){
            title.text = "테스트용 타이틀"
            timestamp.text = "2024.06.21(토) PM02:20"


            btnDelete.setOnClickListener{
               // 삭제 리스너
            }

            itemView.setOnClickListener{
                // 클릭 시 WriteActivity로
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DairyAdopter.DairyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dairies, parent, false)
        return DairyViewHolder(view)
    }

    override fun onBindViewHolder(holder: DairyAdopter.DairyViewHolder, position: Int) {
        holder.onBind()
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
        return 10
    }
}