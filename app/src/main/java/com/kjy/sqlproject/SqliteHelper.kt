package com.kjy.sqlproject

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


// SQLite 데이터베이스 사용을 위해 SQLiteOpenHelper 클래스를 상속받아야 함.
// SQLiteOpenHelper는 생성 시에 Context, 데이터베이스명, 팩토리, 버전정보가 필요함.
// 여기서는 팩토리 x null로 처리
class SqliteHelper(context: Context, name: String, version: Int):
                    SQLiteOpenHelper(context, name, null, version){
    override fun onCreate(db: SQLiteDatabase?) {
        // 데이터베이스 테이블 생성
        val create = "create table memo (" +
                "no integer primary key, " +
                "content text, " +
                "datetime integer" +
                ")"

        db?.execSQL(create)
    }

    // 이 메소드는 SqliteHelper에 전달되는 버전 정보가 변경되었을 때 현재 생성되어 있는 데이터베이스의 버전과 비교해서
    // 더높으면 호출 되는 메소드 이므로 버전 변경 사항이 없으면 호출되지 않음.
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    // 데이터 삽입(INSERT) 메서드 구현
    // SQLiteOpenHelper로 값을 입력할 때는 코틀린 Map 클래스처럼 키, 값 형태로 사용되는 ContentValues 클래스 사용
    // ContentValues에 put("컬럼명", 값)으로 저장해줌
    fun insertMemo(memo: Memo) {
        val values = ContentValues()
        values.put("content", memo.content)
        values.put("datetime", memo.datetime)

        // SQLiteOpenHeler에 이미 구현된 writableDatabase에 테이블명과 함께 앞에서 작성한 값을 전달해 insert()
        // 사용한 후에는 close()로 꼭 닫아줌
        // close() : 데이터베이스 사용시 스마트폰의 시스템 자원을 점유하는데 한 번 점유한 자원은 반드시 close()를 호출해서
        // 반환. close()를 호출하지 않을 경우 반환되지 않아 자원을 낭비할 수 있음.
        val wd = writableDatabase
        wd.insert("memo", null, values)
        wd.close()
    }

    // 조회 메서드(SELECT)
    // 조회 메서드는 반환값이 있으므로 메서드의 가장 윗줄에 반환할 값을 변수로 선언하고, 가장 아랫줄에서 반환하는 코드를 작성한 후
    // 그 사이에 코드를 작성
    fun selectMemo(): MutableList<Memo> {
        val list = mutableListOf<Memo>()

        // 메모의 전체 데이터를 조회하는 쿼리
        val select = "select * from memo"

        // 읽기 전용 데이터베이스를 변수에 담음
        val rd = readableDatabase

        // 데이터베이스의 rawQuery() 메서드에 앞에서 작성해둔 쿼리를 담아서 실행하면 커서 형태로 값이 반환.
        // Cursor 객체는 쿼리에 의하여 생성된 행들을 가리킨다. (현재 위치를 포함하는 데이터 요소)
        val cursor = rd.rawQuery(select, null)

        // 커서의 moveToNext() 메서드가 실행되면 다음 줄에 사용할 수 있는 레코드가 있는지 여부를 반환하고,
        // 해당 커서를 다음 위치로 이동 시킵니다.
        // 레코드가 없으면 반복문을 빠져나가고, 모든 레코드를 읽을 때 까지 반복

        while(cursor.moveToNext()) {
            // 반복문에서 테이블에 정의된 3개의 컬럼에서 값을 꺼낸 후 각각 변수에 담음
            // 컬럼명으로 조회해서 위치값으로 값 꺼내기(몇 번째 컬럼인질르 컬럼명으로 조회)
            val noIdx = cursor.getColumnIndex("no") // 1. 테이블에서 no 컬럼의 순서
            val contentIdx = cursor.getColumnIndex("content")   // 2
            val dateIdx = cursor.getColumnIndex("datetime") // 3

            val no = cursor.getLong(noIdx)      // 값은 위에서 저장해 둔 컬럼의 위치로 가져온다.
            val content = cursor.getString(contentIdx)
            val datetime = cursor.getLong(dateIdx)

            // 변수에 저장해두었던 값들로 Memo 클래스를 생성하고 반환할 목록에 더함.
            list.add(Memo(no, content, datetime))
        }

        // 커서와 읽기 전용 데이터베이스를 모두 닫아줌.
        cursor.close()
        rd.close()

        // 반환
        return list
    }

    // 수정 메서드(Update)
    // INSERT와 동일하게 ContentValues를 사용해서 수정할 값을 저장한다.
    fun updateMemo(memo: Memo) {
        val values = ContentValues()
        values.put("content", memo.content)
        values.put("datetime", memo.datetime)

        // writableDatabase의 update() 메서드를 사용하여 수정후 close()로 닫아줌.
        // update() 메서드의 파라미터는 (테이블명, 수정할 값, 수정할 조건) 순서
        // 수정할 조건은 PRIMARYKEY로 지정된 컬럼을 사용함. 여기서는 PRIMARY KEY인 컬럼이 no이기 때문에 "no = 숫자"
        // 가 된다. 네번째 값은 'null'을 입력한다. 세번째 값을 "no = ?"의 형태로 입력하고,
        // 네 번째에 ?에 매핑할 값을 arrayOf("${memo.no}")의 형태로 전달할 수도 있음.
        // 여기서는 세번째에 조건과 값을 모두 할당했기 때문에 네 번째에 null을 사용하는 것입니다.
        val wd = writableDatabase
        wd.update("memo", values, "no = ${memo.no}", null)
        wd.close()


    }

    // 삭제 메서드(Delete)
    // 조건식은 "컬럼명 = 값"
    fun deleteMemo(memo: Memo) {
        val delete = "delete from memo where no = ${memo.no}"

        // writableDatabase의 execSQL() 메서드로 쿼리 실행 후 close()를 호출
        // execSQL() 메서드로 직접 쿼리문 호출 가능
        val db = writableDatabase
        db.execSQL(delete)
        db.close()
    }

}

// Memo 클래스
// no와 datetime을 Long으로 설정한 이유: 숫자의 범위가 서로 다르기 때문이다.
// 보통 SQLite에서 INTEGER로 선언한 것은 소스 코드에서는 Long으로 사용한다.
// no만 null허용을 한것은 PRIMARY KEY 옵션으로 값이 자동으로 증가하여 데이터 삽입시에는 필요하지 않기 때문임.
data class Memo(var no: Long?, var content: String, var datetime: Long)