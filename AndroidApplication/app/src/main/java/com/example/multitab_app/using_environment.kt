package com.example.multitab_app

import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore.Audio.Media
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.lang.*
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.concurrent.timer

class using_environment : StandardActivity() {
    lateinit var env_data_00 : TextView
    lateinit var env_data_01 : TextView
    lateinit var env_data_02 : TextView
    lateinit var env_data_03 : TextView
    lateinit var env_data_10 : TextView
    lateinit var env_data_11 : TextView
    lateinit var env_data_12 : TextView
    lateinit var env_data_13 : TextView
    lateinit var env_data_20 : TextView
    lateinit var env_data_21 : TextView
    lateinit var env_data_22 : TextView
    lateinit var env_data_23 : TextView

    lateinit var bgm_temp : MediaPlayer
    lateinit var bgm_hum : MediaPlayer
    lateinit var bgm_dust : MediaPlayer



    val sdp = DecimalFormat("#.##")




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

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.using_environment)
        getExtras(intent)
        val usingEnvironment : Button = findViewById(R.id.using_environment)
        val powerUsage : Button = findViewById(R.id.power_usage)
        val onoffControl : Button = findViewById(R.id.onoff_control)
        val logout : Button = findViewById(R.id.tomain)
        val refresh : Button = findViewById(R.id.refresh)

        bgm_temp = MediaPlayer.create(this, R.raw.temperature)
        bgm_hum = MediaPlayer.create(this, R.raw.humidity)
        bgm_dust = MediaPlayer.create(this, R.raw.dust)



        env_data_00 = findViewById(R.id.data_00)
        env_data_01 = findViewById(R.id.data_01)
        env_data_02 = findViewById(R.id.data_02)
        env_data_03 = findViewById(R.id.data_03)
        env_data_10 = findViewById(R.id.data_10)
        env_data_11 = findViewById(R.id.data_11)
        env_data_12 = findViewById(R.id.data_12)
        env_data_13 = findViewById(R.id.data_13)
        env_data_20 = findViewById(R.id.data_20)
        env_data_21 = findViewById(R.id.data_21)
        env_data_22 = findViewById(R.id.data_22)
        env_data_23 = findViewById(R.id.data_23)



        val button_bgm_off : Button = findViewById(R.id.button_bgm_ok)
        val button_alarm_on : Button = findViewById(R.id.button_alarm_on)

        button_bgm_off.setOnClickListener {
            bgm_temp.stop()
            bgm_hum.stop()
            bgm_dust.stop()
            button_bgm_off.setBackgroundColor(Color.rgb(0xC0, 0xC0, 0xF0))
            button_alarm_on.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
        }

        button_alarm_on.setOnClickListener {
            bgm_temp = MediaPlayer.create(this, R.raw.temperature)
            bgm_hum = MediaPlayer.create(this, R.raw.humidity)
            bgm_dust = MediaPlayer.create(this, R.raw.dust)
            button_alarm_on.setBackgroundColor(Color.rgb(0xC0, 0xC0, 0xF0))
            button_bgm_off.setBackgroundColor(Color.rgb(0xdd, 0xdd, 0xdd))
        }

        request_environment()

        //주기적으로 refresh
        val mTimer = Timer()
        var mTimerTask = object : TimerTask() {
            override fun run() {
                val mHandler = Handler(Looper.getMainLooper())
                mHandler.postDelayed({
                    // 반복실행할 구문
                    request_environment()
                }, 0)
            }
        }
        mTimer.schedule(mTimerTask, 0, 3000)

    /*    refresh.setOnClickListener {
            request_environment()
        }*/


        onoffControl.setOnClickListener{
            val to_onoff_control = Intent(this@using_environment, onoff_control::class.java)
            putExtras(to_onoff_control)
            to_onoff_control.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(to_onoff_control)
            //finish()
        }

        powerUsage.setOnClickListener{
            val to_power_usage = Intent(this@using_environment, power_log::class.java)
            putExtras(to_power_usage)
            to_power_usage.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(to_power_usage)
            //finish()
        }

        logout.setOnClickListener{
            val to_power_list = Intent(this, power_list::class.java)
            putExtras(to_power_list)
            to_power_list.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(to_power_list)
            //finish()
        }
    }



    fun request_environment(){
        val thread = NetworkThread_env()
        thread.start()
    }

    inner class NetworkThread_env : NetworkThread() {
        var dens_now_reversed : Double = 0.0
        var dens_day_reversed : Double = 0.0
        var dens_week_reversed : Double = 0.0
        var dens_month_reversed: Double = 0.0
        var tem_now_reversed : Double = 0.0
        var tem_day_reversed : Double = 0.0
        var tem_week_reversed : Double = 0.0
        var tem_month_reversed : Double = 0.0
        var hum_now_reversed : Double = 0.0
        var hum_day_reversed: Double = 0.0
        var hum_week_reversed : Double = 0.0
        var hum_month_reversed : Double = 0.0



        override val loading_dialog: LoadingDialog = LoadingDialog(this@using_environment)

        override fun show_loading() {
            /*runOnUiThread {
                loading_dialog.show()
            }*/

        }
        override fun delete_loading() {
            runOnUiThread {
                loading_dialog.dismiss()
            }
        }

        override val target_host: String = C_HOST

        override fun do_communication(dis: DataInputStream, dos: DataOutputStream) {
            dos.writeByte('8'.code)
            dos.write(id_array_for_send)
            dos.write(login_token_array_for_send)
            dos.write(selected_u_id_array)

            var type = dis.readByte()
            safe_m_err = dis.readByte()


            var tem_month = dis.readDouble()
            var hum_month = dis.readDouble()
            var dens_month = dis.readDouble()
            var tem_week = dis.readDouble()
            var hum_week = dis.readDouble()
            var dens_week = dis.readDouble()
            var tem_day = dis.readDouble()
            var hum_day = dis.readDouble()
            var dens_day = dis.readDouble()
            var tem_now = dis.readDouble()
            var hum_now = dis.readDouble()
            var dens_now = dis.readDouble()




            dens_now_reversed = Double.fromBits(java.lang.Long.reverseBytes(dens_now.toBits()))
            dens_day_reversed = Double.fromBits(java.lang.Long.reverseBytes(dens_day.toBits()))
            dens_week_reversed = Double.fromBits(java.lang.Long.reverseBytes(dens_week.toBits()))
            dens_month_reversed = Double.fromBits(java.lang.Long.reverseBytes(dens_month.toBits()))
            tem_now_reversed = Double.fromBits(java.lang.Long.reverseBytes(tem_now.toBits()))
            tem_day_reversed = Double.fromBits(java.lang.Long.reverseBytes(tem_day.toBits()))
            tem_week_reversed = Double.fromBits(java.lang.Long.reverseBytes(tem_week.toBits()))
            tem_month_reversed = Double.fromBits(java.lang.Long.reverseBytes(tem_month.toBits()))
            hum_now_reversed = Double.fromBits(java.lang.Long.reverseBytes(hum_now.toBits()))
            hum_day_reversed = Double.fromBits(java.lang.Long.reverseBytes(hum_day.toBits()))
            hum_week_reversed = Double.fromBits(java.lang.Long.reverseBytes(hum_week.toBits()))
            hum_month_reversed = Double.fromBits(java.lang.Long.reverseBytes(hum_month.toBits()))

            if (hum_now_reversed > 65f){
                //bgm_hum.isLooping = true
                bgm_hum.start()
            }
            else if (tem_now_reversed > 30f){
                //bgm_temp.isLooping = true
                bgm_temp.start()
            }
            else if (dens_now_reversed > 1050f){
                //bgm_dust.isLooping = true
                bgm_dust.start()
            }
        }

        override fun change_ui() {
            runOnUiThread{
                env_data_00.text = "${sdp.format(dens_now_reversed)}"
                env_data_01.text = "${sdp.format(dens_day_reversed)}"
                env_data_02.text = "${sdp.format(dens_week_reversed)}"
                env_data_03.text = "${sdp.format(dens_month_reversed)}"
                env_data_10.text = "${sdp.format(tem_now_reversed)}"
                env_data_11.text = "${sdp.format(tem_day_reversed)}"
                env_data_12.text = "${sdp.format(tem_week_reversed)}"
                env_data_13.text = "${sdp.format(tem_month_reversed)}"
                env_data_20.text = "${sdp.format(hum_now_reversed)}"
                env_data_21.text = "${sdp.format(hum_day_reversed)}"
                env_data_22.text = "${sdp.format(hum_week_reversed)}"
                env_data_23.text = "${sdp.format(hum_month_reversed)}"
            }
        }
    }
}

