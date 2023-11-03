package com.example.multitab_app

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.SharedPreferences
import android.graphics.Color
import android.net.http.SslCertificate.saveState
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

class onoff_control : StandardActivity() {
    var relay_req : Byte = 0
    lateinit var onAll: Button
    lateinit var offAll: Button
    lateinit var onNum1: Button
    lateinit var offNum1: Button
    lateinit var onNum2: Button
    lateinit var offNum2: Button
    lateinit var onNum3: Button
    lateinit var offNum3: Button

    var num1 = 0
    var num2 = 0
    var num3 = 0

    override fun getExtras(intent: Intent) {
        getExtra_id(intent)
        getExtra_session(intent)
        getExtra_selected_u_id(intent)
    }

    override fun putExtras(intent: Intent) {
        putExtra_id(intent)
        putExtra_session(intent)
        putExtra_selected_u_id(intent)
    }

    fun send_get_relay_req(){
        val thread = NetworkThread_get_relay_req()
        thread.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onoff_control)

        getExtras(intent)

        val usingEnvironment: Button = findViewById(R.id.using_environment)
        val powerUsage: Button = findViewById(R.id.power_usage)
        val logout: Button = findViewById(R.id.tomain)

        onAll = findViewById(R.id.on_all)
        offAll = findViewById(R.id.off_all)
        onNum1 = findViewById(R.id.on_num1)
        offNum1 = findViewById(R.id.off_num1)
        onNum2 = findViewById(R.id.on_num2)
        offNum2 = findViewById(R.id.off_num2)
        onNum3 = findViewById(R.id.on_num3)
        offNum3 = findViewById(R.id.off_num3)

        val submit_button: Button = findViewById(R.id.submit_button)

        send_get_relay_req() // 현재 db내 릴레이 모듈 설정값에 따라 버튼 색 세팅

        onAll.setOnClickListener {
            onNum1.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
            onNum2.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
            onNum3.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
            offNum1.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
            offNum2.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
            offNum3.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))

            num1 = 1
            num2 = 1
            num3 = 1
        }

        offAll.setOnClickListener {
            onNum1.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
            onNum2.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
            onNum3.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
            offNum1.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
            offNum2.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
            offNum3.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))

            num1 = 0
            num2 = 0
            num3 = 0


        }

        onNum1.setOnClickListener {
            onNum1.setBackgroundColor(Color.rgb(0xC0, 0xC0, 0xF0))
            offNum1.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))

            num1 = 1
        }

        onNum2.setOnClickListener {
            onNum2.setBackgroundColor(Color.rgb(0xC0, 0xC0, 0xF0))
            offNum2.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
            num2 = 1
        }

        onNum3.setOnClickListener {
            onNum3.setBackgroundColor(Color.rgb(0xC0, 0xC0, 0xF0))
            offNum3.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
            num3 = 1
        }

        offNum1.setOnClickListener {
            onNum1.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
            offNum1.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
            num1 = 0
        }

        offNum2.setOnClickListener {
            onNum2.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
            offNum2.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
            num2 = 0
        }

        offNum3.setOnClickListener {
            onNum3.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
            offNum3.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
            num3 = 0
        }

        submit_button.setOnClickListener {
            if (num1 == 0 && num2 == 0 && num3 == 0) {
                relay_req = '0'.code.toByte()

            } else if (num1 == 1 && num2 == 0 && num3 == 0) {
                relay_req = '1'.code.toByte()

            } else if (num1 == 0 && num2 == 1 && num3 == 0) {
                relay_req = '2'.code.toByte()

            } else if (num1 == 1 && num2 == 1 && num3 == 0) {
                relay_req = '3'.code.toByte()

            } else if (num1 == 0 && num2 == 0 && num3 == 1) {
                relay_req = '4'.code.toByte()

            } else if (num1 == 1 && num2 == 0 && num3 == 1) {
                relay_req = '5'.code.toByte()

            } else if (num1 == 0 && num2 == 1 && num3 == 1) {
                relay_req = '6'.code.toByte()

            } else if (num1 == 1 && num2 == 1 && num3 == 1) {
                relay_req = '7'.code.toByte()
            }
            Toast.makeText(this, relay_req.toString(), Toast.LENGTH_SHORT).show()
            send_set_relay_req()

        }

        powerUsage.setOnClickListener {
            val nextIntent = Intent(this@onoff_control, power_log::class.java)
            putExtras(nextIntent)
            nextIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(nextIntent)
            //finish()
        }

        usingEnvironment.setOnClickListener {
            val to_using_environment = Intent(this@onoff_control, using_environment::class.java)
            putExtras(to_using_environment)
            to_using_environment.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(to_using_environment)
            overridePendingTransition(0, 0)
            //finish()
        }


        logout.setOnClickListener {
           back_to_power_list()
        }

    }

    fun back_to_power_list(){
        val to_power_list = Intent(this, power_list::class.java)
        putExtras(intent)
        to_power_list.addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(to_power_list)
        //finish()
    }

    fun send_set_relay_req(){
        var thread = NetworkThread_set_relay_req()
        thread.start()
    }
    inner class NetworkThread_get_relay_req : NetworkThread(){
        override val target_host: String = C_HOST
        override val loading_dialog: LoadingDialog = LoadingDialog(this@onoff_control)
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
            dos.writeByte(GET_RELAY_REQ_MESSAGE_TYPE.toInt())
            dos.write(id_array_for_send)
            dos.write(login_token_array_for_send)
            dos.write(selected_u_id_array)

            var type = dis.readByte()
            safe_m_err = dis.readByte()
            relay_req = dis.readByte()
        }
        override fun change_ui() {
            runOnUiThread{
                if (relay_req == '0'.code.toByte()) {
                    num1 = 0
                    num2 = 0
                    num3 = 0
                    onNum1.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    onNum2.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    onNum3.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    offNum1.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    offNum2.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    offNum3.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                } else if (relay_req == '1'.code.toByte()) {
                    num1 = 1
                    num2 = 0
                    num3 = 0
                    onNum1.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    onNum2.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    onNum3.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    offNum1.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    offNum2.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    offNum3.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                } else if (relay_req == '2'.code.toByte()) {
                    num1 = 0
                    num2 = 1
                    num3 = 0
                    onNum1.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    onNum2.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    onNum3.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    offNum1.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    offNum2.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    offNum3.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                } else if (relay_req == '3'.code.toByte()) {
                    num1 = 1
                    num2 = 1
                    num3 = 0
                    onNum1.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    onNum2.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    onNum3.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    offNum1.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    offNum2.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    offNum3.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))

                } else if (relay_req == '4'.code.toByte()) {
                    num1 = 0
                    num2 = 0
                    num3 = 1
                    onNum1.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    onNum2.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    onNum3.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    offNum1.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    offNum2.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    offNum3.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))

                } else if (relay_req == '5'.code.toByte()) {
                    num1 = 1
                    num2 = 0
                    num3 = 1
                    onNum1.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    onNum2.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    onNum3.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    offNum1.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    offNum2.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    offNum3.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                } else if (relay_req == '6'.code.toByte()) {
                    num1 = 0
                    num2 = 1
                    num3 = 1
                    onNum1.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    onNum2.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    onNum3.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    offNum1.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    offNum2.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    offNum3.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                } else if (relay_req == '7'.code.toByte()) {
                    num1 = 1
                    num2 = 1
                    num3 = 1
                    onNum1.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    onNum2.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    onNum3.setBackgroundColor(Color.rgb(0xc0, 0xc0, 0xf0))
                    offNum1.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    offNum2.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                    offNum3.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
                }
            }
        }
    }

    inner class NetworkThread_set_relay_req : NetworkThread(){
        override val target_host: String  = C_HOST
        override val loading_dialog: LoadingDialog = LoadingDialog(this@onoff_control)
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
            dos.writeByte(SET_RELAY_REQ_MESSAGE_TYPE.toInt())
            dos.write(id_array_for_send)
            dos.write(login_token_array_for_send)
            dos.write(selected_u_id_array)
            dos.writeByte(relay_req.toInt())

            var type = dis.readByte()
            safe_m_err = dis.readByte()
        }

        override fun change_ui() {

        }
    }

}




