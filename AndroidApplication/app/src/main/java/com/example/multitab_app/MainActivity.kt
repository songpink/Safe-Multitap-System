
package com.example.multitab_app

import android.app.Application
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.selects.select
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import java.io.IOException
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.UnknownHostException


lateinit var face_bitmap: Bitmap
lateinit var GImageView : ImageView


// 네트워크 쓰레드와 메인 쓰레드(온크리에이트 함수)간 통신용 변수


abstract class StandardActivity : AppCompatActivity(){
    var id_array_for_send = ByteArray(ID_MAX_LENGTH)
    var pw_array_for_send = ByteArray(PW_MAX_LENGTH)
    var login_token_array_for_send = ByteArray(LOGIN_TOKEN_LENGTH)
    var jpg_array = ByteArrayOutputStream()
    var selected_u_id_array = ByteArray(U_ID_LENGTH)

    fun init_id_pw_arrays_for_send(){
        id_array_for_send.fill(0)
        pw_array_for_send.fill(0)
    }

    fun move_to_main_activity(){
        val nextIntent = Intent(this, MainActivity::class.java)
        startActivity(nextIntent)
        finish()
    }

    /*
    fun move_to_power_list_activity(){
        val nextIntent = Intent(this, power_list::class.java)
        nextIntent.putExtra(ID_ARRAY_FOR_SEND_NAME, id_array_for_send)
        nextIntent.putExtra(LOGIN_TOKEN_ARRAY_FOR_SEND_NAME, login_token_array_for_send)
        startActivity(nextIntent)
        finish()
    }
    */
    fun putExtra_id(intent : Intent){
        intent.putExtra(ID_ARRAY_FOR_SEND_NAME, id_array_for_send)
    }
    fun putExtra_pw(intent : Intent){
        intent.putExtra(PW_ARRAY_FOR_SEND_NAME, pw_array_for_send)
    }
    fun putExtra_session(intent : Intent){
        intent.putExtra(LOGIN_TOKEN_ARRAY_FOR_SEND_NAME, login_token_array_for_send)
    }
    fun putExtra_image(intent : Intent){
        intent.putExtra(JPG_ARRAY_NAME, jpg_array.toByteArray())
    }
    fun putExtra_selected_u_id(intent : Intent){
        intent.putExtra(SELECTED_U_ID_ARRAY_NAME, selected_u_id_array)
    }

    fun getExtra_id(intent : Intent){
        if (intent.hasExtra(ID_ARRAY_FOR_SEND_NAME) && (intent.getByteArrayExtra(ID_ARRAY_FOR_SEND_NAME) != null) ) {
            id_array_for_send = intent.getByteArrayExtra(ID_ARRAY_FOR_SEND_NAME)!!
        } else {
            Toast.makeText(this, "전달된 ID가 없습니다", Toast.LENGTH_SHORT).show()
            move_to_main_activity()
        }
    }
    fun getExtra_pw(intent : Intent){
        if (intent.hasExtra(PW_ARRAY_FOR_SEND_NAME) && (intent.getByteArrayExtra(PW_ARRAY_FOR_SEND_NAME) != null) ) {
            pw_array_for_send = intent.getByteArrayExtra(PW_ARRAY_FOR_SEND_NAME)!!
        } else {
            Toast.makeText(this, "전달된 ID가 없습니다", Toast.LENGTH_SHORT).show()
            move_to_main_activity()
        }
    }
    fun getExtra_session(intent : Intent){
        if (intent.hasExtra(LOGIN_TOKEN_ARRAY_FOR_SEND_NAME) && (intent.getByteArrayExtra(LOGIN_TOKEN_ARRAY_FOR_SEND_NAME) != null) ) {
            login_token_array_for_send = intent.getByteArrayExtra(LOGIN_TOKEN_ARRAY_FOR_SEND_NAME)!!
        } else {
            Toast.makeText(this, "전달된 ID가 없습니다", Toast.LENGTH_SHORT).show()
            move_to_main_activity()
        }
    }
    fun getExtra_image(intent : Intent){
        if (intent.hasExtra(JPG_ARRAY_NAME) && (intent.getByteArrayExtra(JPG_ARRAY_NAME) != null) ) {
            jpg_array.write(intent.getByteArrayExtra(JPG_ARRAY_NAME))
        } else {
            Toast.makeText(this, "전달된 ID가 없습니다", Toast.LENGTH_SHORT).show()
            move_to_main_activity()
        }
    }
    fun getExtra_selected_u_id(intent : Intent){
        if (intent.hasExtra(SELECTED_U_ID_ARRAY_NAME) && (intent.getByteArrayExtra(SELECTED_U_ID_ARRAY_NAME) != null) ) {
            selected_u_id_array = intent.getByteArrayExtra(SELECTED_U_ID_ARRAY_NAME)!!
        } else {
            Toast.makeText(this, "전달된 ID가 없습니다", Toast.LENGTH_SHORT).show()
            move_to_main_activity()
        }
    }

    abstract fun putExtras(intent : Intent)
    abstract fun getExtras(intent : Intent)
}


lateinit var activityResult : ActivityResultLauncher<Intent>

class MainActivity : StandardActivity() {
    val TAG: String = "MainActivity"
//    lateinit var getResultImage : ActivityResultLauncher<Intent>


    override fun getExtras(intent: Intent) {
        TODO("Not yet implemented")
    }

    override fun putExtras(intent: Intent) {
        putExtra_id(intent)
        putExtra_pw(intent)
        putExtra_image(intent)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GImageView = findViewById(R.id.imageView3)

        activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK && it.data != null){
                val data = it.data!!.extras
                face_bitmap = data?.get("data") as Bitmap
                face_bitmap.compress(Bitmap.CompressFormat.PNG, 100, jpg_array)
                GImageView.setImageBitmap(face_bitmap)
            }
        }

/*
        getResultImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
            if(result.resultCode == RESULT_OK){
                val UriData: Uri = result.data?.data!!
                if(Build.VERSION.SDK_INT >= 28){
                    val source = ImageDecoder.createSource(contentResolver, UriData)
                    val face_bmp = ImageDecoder.decodeBitmap(source)
                    val GImageView : ImageView = findViewById(R.id.imageView3)
                    GImageView.setImageBitmap(face_bmp)
                    face_bmp.compress(Bitmap.CompressFormat.PNG, 100, jpg_array)
                }
                else{
                    val face_bmp = MediaStore.Images.Media.getBitmap(contentResolver, UriData)
                    val GImageView : ImageView = findViewById(R.id.imageView3)
                    GImageView.setImageBitmap(face_bmp)
                    face_bmp.compress(Bitmap.CompressFormat.PNG, 100, jpg_array)
                }
            }
        }
*/
        // 로그인 버튼
        val Btn_login : Button = findViewById(R.id.btn_login)
        Btn_login.setOnClickListener {


            if(check_id_validation() != true){
                Toast.makeText(this, "올바른 ID를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(check_pw_validation() != true){
                Toast.makeText(this, "올바른 PW를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(jpg_array.size() ==0){
                Toast.makeText(this, "얼굴 사진을 등록해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            init_id_pw_arrays_for_send()
            set_arrays_for_send()

            move_to_login_loading_activity()
        }


        val Btn_face : TextView = findViewById(R.id.select_face)
        Btn_face.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            activityResult.launch(intent)

        //    val intent = Intent()
        //    intent.type ="image/*"
        //    intent.action = Intent.ACTION_GET_CONTENT
        //    getResultImage.launch(intent)
        }

        // 회원가입 버튼
        val Btn_register : Button = findViewById(R.id.btn_to_register)
        Btn_register.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        val Btn_register_wifi : Button = findViewById(R.id.btn_to_register_wifi)
        Btn_register_wifi.setOnClickListener{
            val intent = Intent(this, register_wifi::class.java)
            startActivity(intent)
        }
    }
    fun move_to_login_loading_activity(){
        val nextIntent = Intent(this, LoginLoadingActivity::class.java)
        putExtras(nextIntent)
        startActivity(nextIntent)
        finish()
    }
    fun check_id_validation(): Boolean {
        var Edit_id: EditText = findViewById(R.id.login_id_field)
        var arr = Edit_id.text.toString().toByteArray(Charsets.UTF_8)
        if(arr.size > ID_MAX_LENGTH || arr.size < ID_MIN_LENGTH){
            return false
        }
        else{
            return true
        }
    }


    fun check_pw_validation(): Boolean {
        var Edit_pw: EditText = findViewById(R.id.edit_pw)
        var arr = Edit_pw.text.toString().toByteArray(Charsets.UTF_8)
        if(arr.size < PW_MIN_LENGTH || arr.size > PW_MAX_LENGTH){
            return false
        }
        else{
            return true
        }
    }


    fun set_arrays_for_send(){
        var Edit_id: EditText = findViewById(R.id.login_id_field)
        var Edit_pw: EditText = findViewById(R.id.edit_pw)
        var id_array = Edit_id.text.toString().toByteArray(Charsets.UTF_8)
        var pw_array = Edit_pw.text.toString().toByteArray(Charsets.UTF_8)

        id_array.copyInto(id_array_for_send)
        pw_array.copyInto(pw_array_for_send)
    }
}



