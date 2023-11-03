package com.example.multitab_app


import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.content.DialogInterface
import android.content.Intent
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.io.DataInputStream
import java.io.DataOutputStream
import android.widget.EditText
import android.widget.ImageButton


// 네트워크 쓰레드와 메인 쓰레드(온크리에이트 함수)간 통신용 변수




class power_list : StandardActivity() {
    override fun getExtras(intent: Intent) {
        getExtra_id(intent)
        getExtra_session(intent)
    }

    override fun putExtras(intent: Intent) {
        putExtra_id(intent)
        putExtra_session(intent)
        putExtra_selected_u_id(intent)
    }
    var u_id_array_to_add = ByteArray(U_ID_LENGTH)
    val LIST_MENU : ArrayList<String> = ArrayList<String>()
    lateinit var adapter : ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.power_list)

        getExtras(intent)

        adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, LIST_MENU)

        val listview = findViewById<View>(R.id.power_list_listview) as ListView
        listview.adapter = adapter

        val load_button = findViewById(R.id.power_list_load) as Button
        load_button.setOnClickListener {
            send_get_power_list_message()
        }

        // add button에 대한 이벤트 처리.
        val addButton = findViewById(R.id.power_list_add)  as Button
        addButton.setOnClickListener {
            init_u_id_array_to_add() // 값 전부 0으로 초기화
            if(check_u_id_to_add_validation()!=true){ // 멀티탭 ID 입력란 검사
                Toast.makeText(this, "올바른 멀티탭 ID를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            set_u_id_array_to_add() // 입력란에서 메모리로 가져오기
            send_add_power_message() // 멀티탭 추가 메시지 보내기(UI 업데이트 포함)
        }

        val infoButton = findViewById(R.id.get_power_info)  as ImageButton
        infoButton.setOnClickListener{
            init_selected_u_id_array()
            if(listview.checkedItemCount > 0){
                var checked = listview.checkedItemPosition
                LIST_MENU.get(checked).toByteArray(Charsets.UTF_8).copyInto(selected_u_id_array)
                // selected_u_id_array 설정됨
                move_to_onoff_control()
            }
        }

        val logout_button = findViewById(R.id.logout) as Button
        logout_button.setOnClickListener {
            val to_main = Intent(this, MainActivity::class.java)
            var dialog = AlertDialog.Builder(this)
            dialog.setTitle("LOGOUT")
            dialog.setMessage("정말 로그아웃 하시겠습니까?")
            fun toast_p() {
                Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                startActivity(to_main)
                finish()
            }

            var dialog_listener = object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE ->
                            toast_p()
                    }
                }
            }
            dialog.setPositiveButton("CONFIRM", dialog_listener)
            dialog.setNegativeButton("CANCEL ", null)
            dialog.show()
            overridePendingTransition(0, 0)
        }
    }
    fun move_to_onoff_control(){ // 액티비티 종료 안함.
        val nextIntent = Intent(this, onoff_control::class.java)
        putExtras(nextIntent)
        startActivity(nextIntent)
    }
    fun send_add_power_message(){
        var thread = NetworkThread_add_power()
        thread.start()
    }

    fun send_get_power_list_message(){
        var thread = NetworkThread_get_power_list()
        thread.start()
    }


    fun init_u_id_array_to_add(){
        u_id_array_to_add.fill(0)
    }

    fun init_selected_u_id_array(){
        selected_u_id_array.fill(0)
    }

    fun check_u_id_to_add_validation() : Boolean{
        val u_id_field_view = findViewById(R.id.u_id_field) as EditText
        var arr = u_id_field_view.text.toString().toByteArray(Charsets.UTF_8)

        if(arr.size == U_ID_LENGTH){
            return true
        }
        else{
            return false
        }
    }

    fun set_u_id_array_to_add(){
        val u_id_field_view = findViewById(R.id.u_id_field) as EditText
        var arr = u_id_field_view.text.toString().toByteArray(Charsets.UTF_8)

        arr.copyInto(u_id_array_to_add)
    }

    inner class NetworkThread_get_power_list : NetworkThread() {
        override val target_host: String = C_HOST
        override val loading_dialog: LoadingDialog = LoadingDialog(this@power_list)
        override fun show_loading() {
            runOnUiThread {
                loading_dialog.show()
            }
        }

        override fun delete_loading() {
            runOnUiThread {
                loading_dialog.dismiss()
            }
        }
        override fun do_communication(dis: DataInputStream, dos: DataOutputStream) {
            dos.writeByte(GET_POWER_LIST_MESSAGE_TYPE.toInt())
            dos.write(id_array_for_send)
            dos.write(login_token_array_for_send)

            var type = dis.readByte()
            safe_m_err = dis.readByte()

            var num_power = java.lang.Integer.reverseBytes(dis.readInt())

            LIST_MENU.clear()
            if (num_power > 0) {
                for (i in 0..num_power - 1) {
                    var temp_array = ByteArray(U_ID_LENGTH)
                    dis.readFully(temp_array)
                    LIST_MENU.add(temp_array.toString(Charsets.UTF_8))
                }
            }
        }

        override fun change_ui() {
            runOnUiThread{
                adapter.notifyDataSetChanged()
            }
        }
    }
    inner class NetworkThread_add_power : NetworkThread() {
        override val target_host: String = C_HOST
        override val loading_dialog: LoadingDialog = LoadingDialog(this@power_list)
        override fun show_loading() {
            runOnUiThread {
                loading_dialog.show()
            }
        }

        override fun delete_loading() {
            runOnUiThread {
                loading_dialog.dismiss()
            }
        }
        override fun do_communication(dis: DataInputStream, dos: DataOutputStream) {
            dos.writeByte(ADD_POWER_MESSAGE_TYPE.toInt())
            dos.write(id_array_for_send)
            dos.write(login_token_array_for_send)
            dos.write(u_id_array_to_add) // 추가 버튼을 눌렀을 때 제대로된 값이 배열에 들어옴.

            var type = dis.readByte()
            safe_m_err = dis.readByte()
        }

        override fun change_ui() {

        }
    }
}



