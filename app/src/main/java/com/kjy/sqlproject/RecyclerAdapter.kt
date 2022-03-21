package com.kjy.sqlproject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kjy.sqlproject.databinding.ItemRecyclerBinding
import java.text.SimpleDateFormat


// RecyclerAdapter 만들기

class RecyclerAdapter: RecyclerView.Adapter<Holder>() {
    var listData = mutableListOf<Memo>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context),
                                                parent, false)

        return Holder(binding)

    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val memo = listData.get(position)
        holder.setMemo(memo)
    }


}

// Holder 클래스 생성
class Holder(val binding: ItemRecyclerBinding): RecyclerView.ViewHolder(binding.root) {
    fun setMemo(memo: Memo) {
        binding.textNo.text = "${memo.no}"
        binding.textContent.text = memo.content
        val sdf = SimpleDateFormat("yyyy/MM/dd hh:mm")
        // 날짜 포맷은 SimpleDateFormat으로 설정
        binding.textDatetime.text = "${sdf.format(memo.datetime)}"
    }
}