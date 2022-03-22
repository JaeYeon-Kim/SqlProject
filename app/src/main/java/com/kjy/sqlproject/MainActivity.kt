package com.kjy.sqlproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.kjy.sqlproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // SqliteHelper를 생성하고 변수에 저장
    val helper = SqliteHelper(this, "memo", 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // RecyclerAdapter를 생성
        val adapter = RecyclerAdapter()

        // helper를 어댑터에 전달해줌
        adapter.helper = helper

        // adapter의 listData에 데이터베이스에서 가져온 데이터를 셋팅
        adapter.listData.addAll(helper.selectMemo())

        // 화면의 리사이클러뷰 위젯에 adapter를 연결하고 레이아웃 매니저를 설정
        binding.recyclerMemo.adapter = adapter
        binding.recyclerMemo.layoutManager = LinearLayoutManager(this)

        // 저장 버튼에 클릭리스너를 연결
        binding.buttonSave.setOnClickListener {
            // 메모를 입력하는 플레인텍스트를 검사해서 값이 있으면 해당 내용으로 Memo 클래스를 생성
            if (binding.editMemo.text.toString().isNotEmpty()) {
                val memo = Memo(null, binding.editMemo.text.toString(), System.currentTimeMillis())

                // helper 클래스의 insertMemo() 메서드 앞에서 생성한 Memo를 전달해 데이터베이스에 저장
                helper.insertMemo(memo)

                // 어댑터 데이터 초기화
                adapter.listData.clear()

                // 데이터베이스에서 새로운 목록을 읽어와 어댑터에 세팅하고 갱신함.
                adapter.listData.addAll(helper.selectMemo())
                // 새로 생성되는 메모에는 번호가 자동 입력되므로 번호를 갱신하기 위해서 새로운 데이터를 셋팅
                adapter.notifyDataSetChanged()

                // 메모 내용을 입력하는 위젯의 내용을 지워서 초기화
                binding.editMemo.setText("")
            }
        }


    }
}