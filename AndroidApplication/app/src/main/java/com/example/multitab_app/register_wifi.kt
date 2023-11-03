package com.example.multitab_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import java.io.IOException
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.UnknownHostException



class register_wifi : StandardActivity() {
    var wifi_ssid :String = ""
    var wifi_pw : String = ""
    override fun getExtras(intent: Intent) {
        TODO("Not yet implemented")
    }

    override fun putExtras(intent: Intent) {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_wifi)

        val send_wifi : Button = findViewById(R.id.send_wifi)
        send_wifi.setOnClickListener{
            init_wifi_strings_for_send()

            if(check_wifi_ssid_validation() != true){
                Toast.makeText(this, "올바른 SSID를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(check_wifi_pw_validation() != true){
                Toast.makeText(this, "올바른 PW를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            set_wifi_strings_for_send()
            send_wifi_set_message() // 블로킹 함수, 차후 exception 처리 필요

            Toast.makeText(this, "와이파이가 설정되었습니다.", Toast.LENGTH_SHORT).show()
        }

        val to_main : Button = findViewById(R.id.register_wifi_to_main)
        to_main.setOnClickListener {
            val to_main_intent = Intent(this, MainActivity::class.java)
            to_main_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(to_main_intent)
            overridePendingTransition(0, 0)
            finish()
        }
    }

    fun init_wifi_strings_for_send(){
        wifi_ssid = ""
        wifi_pw = ""
    }

    fun check_wifi_ssid_validation() : Boolean{
        var ssid: EditText = findViewById(R.id.register_wifi_ssid_field)
        var arr = ssid.text.toString().toByteArray(Charsets.UTF_8)
        if(arr.size < 1){
            return false
        }
        else{
            return true
        }
    }

    fun check_wifi_pw_validation() : Boolean{
        var pw: EditText = findViewById(R.id.register_wifi_pw)
        var arr = pw.text.toString().toByteArray(Charsets.UTF_8)
        if(arr.size < 0 || arr.size > WIFI_PW_MAX_LENGTH){
            return false
        }
        else{
            return true
        }
    }

    fun set_wifi_strings_for_send(){
        var ssid: EditText = findViewById(R.id.register_wifi_ssid_field)
        var pw: EditText = findViewById(R.id.register_wifi_pw)

        wifi_ssid = ssid.text.toString()
        wifi_pw = pw.text.toString()

    }

    fun send_wifi_set_message(){
        var thread = NetworkThread_register_wifi()
        thread.start()
    }
    inner class NetworkThread_register_wifi : NetworkThread() {
        override val target_host: String = ESP_HOST
        override val loading_dialog: LoadingDialog = LoadingDialog(this@register_wifi)
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
            dos.write(wifi_ssid.toByteArray(Charsets.UTF_8))
            dos.writeByte(' '.code)
            dos.write(wifi_pw.toByteArray(Charsets.UTF_8))
            dos.writeByte('&'.code)
        }

        override fun change_ui() {
            runOnUiThread{
                if(is_socket_err_success() != true){
                    print_message_from_socket_err(applicationContext)
                }
                else{
                    //move_to_power_list_activity()
                    Toast.makeText(applicationContext, "와이파이 설정 요청을 보냈습니다.", Toast.LENGTH_SHORT)
                }
            }
        }
    }
}

