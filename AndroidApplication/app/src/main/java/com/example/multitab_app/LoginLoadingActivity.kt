
package com.example.multitab_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import java.io.IOException
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class NetworkThread : Thread(){
    var safe_m_err : Byte = SAFE_M_SUCCESS
    var socket_err = SOCKET_SUCCESS
    abstract val target_host : String
    abstract val loading_dialog : LoadingDialog
    abstract fun show_loading()
    abstract fun delete_loading()
    fun reverse_int(i : Int) : Int{
        return java.lang.Integer.reverseBytes(i)
    }
    override fun run() {
        show_loading()
        try {
            var socket = Socket(target_host, PORT)
            socket.soTimeout = SOCKET_TIME_FOR_READ
            var dis = DataInputStream(socket.getInputStream())
            var dos = DataOutputStream(socket.getOutputStream())
            init_to_send_messages()
            do_communication(dis, dos)
            socket.close()
        } catch (e: java.net.SocketException) { // 알 수 없는 오류
            socket_err = SOCKET_UNKNOWN_ERROR
        }
        catch (e: UnknownHostException) { // 서버 연결 실패
            socket_err = SOCKET_UNKNOWN_SERVER
        }
        catch (e: SocketTimeoutException) { // read 타임 아웃
            socket_err = SOCKET_READ_TIMEOUT
        }
        catch (e: IOException) { // 알 수 없는 오류
            socket_err = SOCKET_UNKNOWN_ERROR
        }

        catch (e: EOFException) { // 서버 측에서 tcp 연결 종료
            socket_err = SOCKET_UNKNOWN_ERROR
        }

        catch (e: Exception){
            socket_err = SOCKET_UNKNOWN_ERROR
        }

        change_ui()
        delete_loading()
    }
    abstract fun do_communication(dis : DataInputStream, dos : DataOutputStream)
    abstract fun change_ui()

    fun init_to_send_messages(){
        socket_err = SOCKET_SUCCESS
        safe_m_err = SAFE_M_SUCCESS
    }

    fun print_message_from_socket_err(con : android.content.Context) {
        if(socket_err != SOCKET_SUCCESS){
            if(socket_err == SOCKET_UNKNOWN_ERROR){
                Toast.makeText(con, "알 수 없는 오류입니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
            else if(socket_err == SOCKET_READ_TIMEOUT){
                Toast.makeText(con, "네트워크 연결 상태를 확인해주세요.", Toast.LENGTH_SHORT).show()
            }
            else if(socket_err == SOCKET_UNKNOWN_SERVER) {
                Toast.makeText(con, "네트워크 연결 상태가 좋지 않거나 서버가 오프라인입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun is_socket_err_success() : Boolean{
        if(socket_err != SOCKET_SUCCESS){
            return false
        }
        else{
            return true
        }
    }

    fun print_message_from_safe_m_err(con : android.content.Context){
        if(safe_m_err == SAFE_M_DUPLICATED_ID){
            Toast.makeText(con, "이미 존재하는 ID 입니다.", Toast.LENGTH_SHORT).show()
        }
        else if(safe_m_err == SAFE_M_INVALID_ID_PW){
            Toast.makeText(con, "ID, PW를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
        }
        else if(safe_m_err == SAFE_M_ID_NOT_EXISTS){
            Toast.makeText(con, "존재하지 않는 ID입니다.", Toast.LENGTH_SHORT).show()
        }
        else if(safe_m_err == SAFE_M_INVALID_TOKEN){
            Toast.makeText(con, "로그인 세션이 만료되었습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
        }
        else if(safe_m_err == SAFE_M_POWER_NOT_EXISTS){
            Toast.makeText(con, "존재하지 않는 멀티탭 ID입니다.", Toast.LENGTH_SHORT).show()
        }
        else if(safe_m_err == SAFE_M_USER_NO_OWN_TO_POWER){
            Toast.makeText(con, "멀티탭을 등록한 후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
        }
        else if(safe_m_err == SAFE_M_SQL_FAIL){
            Toast.makeText(con, "서버의 데이터 베이스에 접근할 수 없습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    fun is_safe_m_err_success() : Boolean{
        if(safe_m_err != SAFE_M_SUCCESS){
            return false
        }
        else{
            return true
        }
    }
}

class LoginLoadingActivity : StandardActivity() {
    override fun getExtras(intent: Intent) {
        getExtra_id(intent)
        getExtra_pw(intent)
        getExtra_image(intent)
    }

    override fun putExtras(intent: Intent) {
        putExtra_id(intent)
        putExtra_session(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_loading)

        getExtras(intent)

        send_login_message()
    }


    fun send_login_message(){
        val thread = NetworkThread_login()
        thread.start()
    }
    fun move_to_power_list_activity(){
        val nextIntent = Intent(this, power_list::class.java)
        putExtras(nextIntent)
        startActivity(nextIntent)
        finish()
    }
    inner class NetworkThread_login : NetworkThread() {
        override val target_host: String = PY_HOST
        override val loading_dialog: LoadingDialog = LoadingDialog(this@LoginLoadingActivity)
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
        override fun change_ui() {
            runOnUiThread{
                if(is_socket_err_success() != true){
                    print_message_from_socket_err(applicationContext)
                    move_to_main_activity()
                }
                else if(is_safe_m_err_success() != true){
                    print_message_from_safe_m_err(applicationContext)
                    move_to_main_activity()
                }
                else{
                    //
                    Toast.makeText(applicationContext, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()
                    move_to_power_list_activity()
                }
            }
        }

        override fun do_communication(dis: DataInputStream, dos: DataOutputStream) {
            var size = jpg_array.size()
            //Log.v("jpg size: ", size.toString())
            size = java.lang.Integer.reverseBytes(size)

            dos.writeByte(LOGIN_MESSAGE_TYPE.toInt())
            dos.write(id_array_for_send)
            dos.write(pw_array_for_send)
            dos.writeInt(size)
            dos.write(jpg_array.toByteArray())

            var type = dis.readByte()
            safe_m_err = dis.readByte()

            dis.readFully(login_token_array_for_send, 0, LOGIN_TOKEN_LENGTH)
        }
    }
}



