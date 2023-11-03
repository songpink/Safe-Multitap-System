package com.example.multitab_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.MediaStore.Audio
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.Timer
import java.util.TimerTask

class MyService : Service() {

    val CHANNEL_ID = "FG5153"
    val NOTI_ID = 153

    val login_id_serv = ""
    val login_token_serv = ""
    val selected_u_serv = ""
    var login_id_service = login_id_serv.toByteArray(Charsets.UTF_8)
    var login_token_service = login_token_serv.toByteArray(Charsets.UTF_8)
    var selected_u_service = selected_u_serv.toByteArray(Charsets.UTF_8)
    var dens_now_reversed: Double = 0.0
    var tem_now_reversed: Double = 1.0
    var hum_now_reversed: Double = 1.0
    var service_output =""

    fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val serviceChannel = NotificationChannel(CHANNEL_ID, "FOREGROUND", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
/*    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val login_id_serv_ex = intent!!.getByteArrayExtra("lis")
        val login_token_serv_ex = intent!!.getByteArrayExtra("lts")
        val selected_u_serv_ex = intent!!.getByteArrayExtra("sus")


        val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

        login_id_service = login_id_serv_ex!!
        login_token_service = login_token_serv_ex!!
        selected_u_service = selected_u_serv_ex!!

        val mTimer = Timer()
        var mTimerTask = object : TimerTask() {
            override fun run() {
                val mHandler = Handler(Looper.getMainLooper())
                mHandler.postDelayed({
                    var thread = NetThread()
                    thread.start()

                    if ((tem_now_reversed < 0)||(tem_now_reversed>30)||(hum_now_reversed<10)||(hum_now_reversed>55))
                        if(dens_now_reversed > 150){
                            service_output = "온, 습도 미세먼지 농도가 비정상으로 감지\n 어플을 열어 상태를 확인해 주세요"
                            var notification = NotificationCompat.Builder(this@MyService, CHANNEL_ID)
                                .setContentText("$service_output")
                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                .build()

                            startForeground(NOTI_ID, notification)
                        }
                        else {
                            service_output = "멀티탭 주변의 온, 습도 조치가 필요\n어플을 열어 상태를 확인해 주세요"
                            var notification =
                                NotificationCompat.Builder(this@MyService, CHANNEL_ID)
                                    .setContentText("$service_output")
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .build()

                            startForeground(NOTI_ID, notification)
                        }
                    else if (dens_now_reversed < 150){
                        service_output = "실내사 미세먼지 농도가 비정상으로 감지\n어플을 열어 상태를 확인해 주세요"
                        var notification = NotificationCompat.Builder(this@MyService, CHANNEL_ID)
                            .setContentText("$service_output")
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .build()

                        startForeground(NOTI_ID, notification)
                    }




                    // 반복실행할 구문
                }, 0)
            }
        }
        mTimer.schedule(mTimerTask, 0, 5000)

        return super.onStartCommand(intent, flags, startId)


    }*/


    inner class NetThread: NetworkThread() {

        override val loading_dialog: LoadingDialog = LoadingDialog(this@MyService)

        override fun show_loading(){}

        override fun delete_loading(){}


        override val target_host: String = C_HOST

        override fun do_communication(dis: DataInputStream, dos: DataOutputStream) {
            dos.writeByte('8'.code)
            dos.write(login_id_service)
            dos.write(login_token_service)
            dos.write(selected_u_service)

            var type = dis.readByte()
            safe_m_err = dis.readByte()

            var tem_now = dis.readDouble()
            var hum_now = dis.readDouble()
            var dens_now = dis.readDouble()


            dens_now_reversed = Double.fromBits(java.lang.Long.reverseBytes(dens_now.toBits()))
            tem_now_reversed = Double.fromBits(java.lang.Long.reverseBytes(tem_now.toBits()))
            hum_now_reversed = Double.fromBits(java.lang.Long.reverseBytes(hum_now.toBits()))

        }
        override fun change_ui() { }
    }
    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }
}