package com.example.multitab_app

import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.EnumSet.range
import java.util.Timer
import java.util.TimerTask


class power_log : StandardActivity() {
    lateinit var bgm_bug : MediaPlayer
    lateinit var bgm_mold : MediaPlayer

    var log_period : Int = 0
    lateinit var log_view : TextView
    override fun getExtras(intent: Intent) {
        getExtra_selected_u_id(intent)
        getExtra_id(intent)
        getExtra_session(intent)
    }

    override fun putExtras(intent: Intent) {
        putExtra_selected_u_id(intent)
        putExtra_id(intent)
        putExtra_session(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.power_log)
        getExtras(intent)

        bgm_bug = MediaPlayer.create(this, R.raw.bug)
        bgm_mold = MediaPlayer.create(this, R.raw.mold)

        val button_bgm_off : Button = findViewById(R.id.button_bgm_ok)
        val button_alarm_on : Button = findViewById(R.id.button_alarm_on)

        button_bgm_off.setOnClickListener {
            bgm_bug.stop()
            bgm_mold.stop()
            button_bgm_off.setBackgroundColor(Color.rgb(0xC0, 0xC0, 0xF0))
            button_alarm_on.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
        }

        button_alarm_on.setOnClickListener {
            bgm_bug = MediaPlayer.create(this, R.raw.bug)
            bgm_mold = MediaPlayer.create(this, R.raw.mold)
            button_alarm_on.setBackgroundColor(Color.rgb(0xC0, 0xC0, 0xF0))
            button_bgm_off.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
        }

        //버튼 뷰 객체 생성
        val usingEnvironment: Button = findViewById(R.id.using_environment)
        val onoffControl: Button = findViewById(R.id.onoff_control)
        val logout: Button = findViewById(R.id.tomain)
        val request_log_button : Button = findViewById(R.id.request_log_button)

        //텍스트 뷰 객체 생성
        log_view = findViewById(R.id.log_text)
        log_view.movementMethod = ScrollingMovementMethod()
        val log_period_view : TextView = findViewById(R.id.log_period_field)

        val mTimer = Timer()
        var mTimerTask = object : TimerTask() {
            override fun run() {
                val mHandler = Handler(Looper.getMainLooper())
                mHandler.postDelayed({
                    // 반복실행할 구문
                    var thread = NetworkThread_request_log()
                    thread.start()
                }, 0)
            }
        }
        mTimer.schedule(mTimerTask, 0, 5000)


        request_log_button.setOnClickListener{
            /*
            try {
                log_period = log_period_view.text.toString().toInt()
                Toast.makeText(applicationContext, "값 : " + log_period.toString(), Toast.LENGTH_SHORT).show()
            }catch(e : NumberFormatException){
                Toast.makeText(applicationContext, "올바른 값을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }*/

            // 쓰레드를 통해 통신.
            var thread = NetworkThread_request_log()
            thread.start()
        }

        usingEnvironment.setOnClickListener {
            val to_using_environment = Intent(this, using_environment::class.java)
            putExtras(to_using_environment)
            to_using_environment.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(to_using_environment)
            overridePendingTransition(0, 0)
            //finish()
        }

        onoffControl.setOnClickListener {
            val nextIntent = Intent(this, onoff_control::class.java)
            putExtras(nextIntent)
            nextIntent.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(nextIntent)
            //finish()
        }

        logout.setOnClickListener {
            val to_power_list = Intent(this, power_list::class.java)
            putExtras(to_power_list)
            to_power_list.addFlags(FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(to_power_list)
            //finish()
        }
    }
    inner class NetworkThread_request_log : NetworkThread(){
        override val loading_dialog: LoadingDialog = LoadingDialog(this@power_log)
        override val target_host: String = PY_HOST
        var log_str : String = ""


        override fun show_loading() {
    /*        runOnUiThread{
                loading_dialog.show()
            }*/
        }

        override fun delete_loading() {
            runOnUiThread{
                loading_dialog.dismiss()
            }
        }

        override fun do_communication(dis: DataInputStream, dos: DataOutputStream) {
            val log_period_reversed = reverse_int(24)
            dos.writeByte(REQUEST_LOG_MESSAGE_TYPE.toInt())
            dos.write(id_array_for_send)
            dos.write(login_token_array_for_send)
            dos.write(selected_u_id_array)
            dos.writeInt(log_period_reversed)

            var type = dis.readByte()
            safe_m_err = dis.readByte()
            val output = ArrayList<Byte>()
            // DB 내의 로그 메시지들을 표현하는 바이트열 받기
            while(true){
                var temp = dis.readByte()
                if(temp != '\u0001'.code.toByte()){
                    output.add(temp)
                }
                else{
                    break
                }
            }
            // 받은 바이트열을 문자열로 변환
            log_str = output.toByteArray().toString(Charsets.UTF_8)
            var left_index = 0
            var right_index = 0
            var logs = ArrayList<String>()
            for(i in 0..log_str.length-1){
                if(log_str.get(i) == '\u0000'){
                    right_index = i
                    logs.add(log_str.slice(IntRange(left_index, right_index)))
                    left_index = right_index + 1
                }
            }
            var final_index = logs.size - 1
            var str : String = logs.get(final_index)
            var received_date_str = str.slice(IntRange(str.indexOf(":::") + 4, str.lastIndex - 1))
            var received_date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(received_date_str)
            var now = System.currentTimeMillis()
            var diff_time = (now - received_date.time) / (1000.0)
            runOnUiThread{
                //Toast.makeText(applicationContext, "date :" + received_date_str + "diff : " + diff_time, Toast.LENGTH_SHORT).show()
            }

            if(diff_time < 15.0){
                if (str.contains("벌레")){
                    bgm_bug.start()
                }
                else if (str.contains("곰팡이")){
                    bgm_mold.start()
                }
            }

        }

        override fun change_ui() {
            runOnUiThread{
                val nl_log = log_str.replace("\u0000", "\n")
                log_view.text = nl_log
                //Toast.makeText(applicationContext, nl_log, Toast.LENGTH_SHORT).show()
                //val testtext : TextView = findViewById(R.id.testtext)
                //val testtext2 : TextView = findViewById(R.id.testtext2)
                //testtext.text = "$log_str"
                //testtext2.text = "$nl_log"
                var logs = emptyList<String>()

            }
        }
    }
}