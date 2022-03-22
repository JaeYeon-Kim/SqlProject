package com.kjy.sqlproject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kjy.sqlproject.databinding.ItemRecyclerBinding
import java.text.SimpleDateFormat


// RecyclerAdapter 만들기

class RecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.Holder>() {
    // helper 프로퍼티 생성
    var helper: SqliteHelper? = null

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

    // Holder 클래스 생성
    // 기존의 Holder클래스를 어댑터 밖에 선언했기 때문에 어댑터 안쪽으로 옮겨주고 inner 클래스로 선언하여
    // 어댑터의 멤버 변수에 접근할 수 있도록 함.
    // 홀더는 한 화면에 그려지는 개수만큼 만든 후 재사용하므로 1번 메모가 있는 홀더를 스크롤해서 위로 올리면 아래에서 올라오는
    // 새로운 메모가 1번 홀더를 재사용 하는 구조이기 때문에 클릭 하는 시점에 어떤 데이터가 있는지 알아야 함.
    inner class Holder(val binding: ItemRecyclerBinding): RecyclerView.ViewHolder(binding.root) {
        // setMemo() 메서드로 넘어온 Memo를 임시저장
        var mMemo: Memo? = null

        init {
            binding.buttonDelete.setOnClickListener {
                // SQLite의 데이터를 먼저 삭제하고, listData의 데이터도 삭제하고 , 어댑터를 갱신함.
                // deleteMemo() 는 null을 허용하지 않지만, mMemo는 null을 허용하도록 설정되었기때문에
                // !!를 사용해서 강제적으로 해야한다.
                helper?.deleteMemo(mMemo!!)
                listData.remove(mMemo)
                notifyDataSetChanged()


            }
        }
        fun setMemo(memo: Memo) {
            binding.textNo.text = "${memo.no}"
            binding.textContent.text = memo.content
            val sdf = SimpleDateFormat("yyyy/MM/dd hh:mm")
            // 날짜 포맷은 SimpleDateFormat으로 설정
            binding.textDatetime.text = "${sdf.format(memo.datetime)}"
            this.mMemo = memo
        }
    }


}
