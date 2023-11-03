package com.example.multitab_app

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import java.io.IOException
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.UnknownHostException

lateinit var myid : String
lateinit var mypw : String



class Register : StandardActivity() {
    var pattern = "^[a-zA-Z]+[0-9]+|[0-9]+[a-zA-Z]+$".toRegex()
    val TAG: String = "Register"
    var isExistBlank = false
    var isPWSame = false
    var isExistLess = false
    var isalphamix = false
    override fun getExtras(intent: Intent) {
        TODO("Not yet implemented")
    }

    override fun putExtras(intent: Intent) {
        TODO("Not yet implemented")
    }
    lateinit var getResultImage : ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        getResultImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == RESULT_OK){
                val UriData: Uri = result.data?.data!!
                if(Build.VERSION.SDK_INT >= 28){
                    val source = ImageDecoder.createSource(contentResolver, UriData)
                    val face_bmp = ImageDecoder.decodeBitmap(source)
                    val GImageView : ImageView = findViewById(R.id.imageView)
                    GImageView.setImageBitmap(face_bmp)
                    face_bmp.compress(Bitmap.CompressFormat.PNG, 100, jpg_array)
                }
                else{
                    val face_bmp = MediaStore.Images.Media.getBitmap(contentResolver, UriData)
                    val GImageView : ImageView = findViewById(R.id.imageView)
                    GImageView.setImageBitmap(face_bmp)
                    face_bmp.compress(Bitmap.CompressFormat.PNG, 100, jpg_array)
                }
            }
        }
        // 로그인 창으로 돌아가기
        val Register_to_main: Button = findViewById(R.id.register_to_main)
        Register_to_main.setOnClickListener {
            finish()
        }

        val Btn_face : TextView = findViewById(R.id.select_face2)
        Btn_face.setOnClickListener {
            val intent = Intent()
            intent.type ="image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            getResultImage.launch(intent)
        }

        val Btn_register2: Button = findViewById(R.id.btn_register)
        Btn_register2.setOnClickListener {
            Log.d(TAG, "회원가입 버튼 클릭")

            val Edit_id: EditText = findViewById(R.id.register_id_field)
            myid = Edit_id.text.toString()
            val Edit_pw: EditText = findViewById(R.id.register_edit_pw)
            mypw = Edit_pw.text.toString()
            val Edit_pw_re: EditText = findViewById(R.id.edit_pw_re)
            val pw_re = Edit_pw_re.text.toString()

            // 유저가 항목을 다 채우지 않았을 경우
            if (myid.isEmpty() || mypw.isEmpty() || pw_re.isEmpty()) {
                isExistBlank = true
            }
            if ((myid.length >= 5)&&(myid.length <=8) && (mypw.length >=8) && (mypw.length <=16) && (8 <=pw_re.length) && (pw_re.length <= 16)) {
                isExistLess = true
            }
            if((myid.matches(pattern))&&(mypw.matches(pattern))&&(pw_re.matches(pattern)))
            {
                  isalphamix = true
            }
            if (mypw == pw_re) {
                isPWSame = true
              }
            var asd = myid.matches(pattern)
            var asdf = mypw.matches(pattern)
            var asdfg = pw_re.contains(pattern)
            println ("ssss" + "$myid"+ "$asd"+"$asdf"+"$asdfg"+"$isalphamix")

            if((!isExistBlank) && isPWSame && isExistLess && isalphamix){
                // 클라이언트 내 적합성 테스트 통과 후
                if(jpg_array.size() ==0){
                    Toast.makeText(this, "얼굴 사진을 등록해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                init_id_pw_arrays_for_send()
                set_arrays_for_send()

                send_register_message()
            }
            else{

                // 상태에 따라 다른 다이얼로그 띄워주기
                if(isExistBlank){   // 작성 안한 항목이 있을 경우
                    dialog("blank")
                }
                else if(!isExistLess){
                    dialog("less")
                }
                else if(!isalphamix){
                    dialog("mix")
                }

                else if(!isPWSame){ // 입력한 비밀번호가 다를 경우
                    dialog("not same")

                }
            }
        }
    }

    // 회원가입 실패시 다이얼로그를 띄워주는 메소드
    fun dialog(type: String) {
        val dialog = AlertDialog.Builder(this)

        // 작성 안한 항목이 있을 경우
        if (type.equals("blank")) {
            dialog.setTitle("회원가입 실패")
            dialog.setMessage("입력란을 모두 작성해주세요")
            isExistBlank = false
        }
        // 입력한 비밀번호가 다를 경우
        else if (type.equals("not same")) {
            dialog.setTitle("회원가입 실패")
            dialog.setMessage("비밀번호가 다릅니다")
        } else if (type.equals("less")) {
            dialog.setTitle("회원가입 실패")
            dialog.setMessage("아이디는 5~8자, 비밀번호는 8~16자 길이로 작성해 주셔야 합니다. ")
        } else if (type.equals("mix")){
            dialog.setTitle("회원가입 실패")
            dialog.setMessage("아이디와 비밀번호 작성 시 영어와 숫자를 혼용해야 합니다")
            }
        val dialog_listener = object: DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                when(which){
                    DialogInterface.BUTTON_POSITIVE ->
                        Log.d(TAG, "다이얼로그")
                }
            }
        }
        println("Sss" + "$isExistBlank" +"$isExistLess" + "$isalphamix" + "$isPWSame")
        dialog.setPositiveButton("확인",dialog_listener)
        dialog.show()
    }

    fun set_arrays_for_send(){
        var Edit_id: EditText = findViewById(R.id.register_id_field)
        var Edit_pw: EditText = findViewById(R.id.register_edit_pw)
        var id_array = Edit_id.text.toString().toByteArray(Charsets.UTF_8)
        var pw_array = Edit_pw.text.toString().toByteArray(Charsets.UTF_8)

        id_array.copyInto(id_array_for_send)
        pw_array.copyInto(pw_array_for_send)
    }

    fun send_register_message(){
        var thread = NetworkThread_register()
        thread.start()
    }

    fun move_from_register_to_main_activity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        finish()
    }

    inner class NetworkThread_register : NetworkThread() {
        override val target_host: String = PY_HOST
        override val loading_dialog: LoadingDialog = LoadingDialog(this@Register)
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
            var size = jpg_array.size()
            Log.v("jpg size: ", size.toString())
            size = java.lang.Integer.reverseBytes(size)

            dos.writeByte(REGISTER_MESSAGE_TYPE.toInt())
            dos.write(id_array_for_send)
            dos.write(pw_array_for_send)
            dos.writeInt(size)
            dos.write(jpg_array.toByteArray())

            var type = dis.readByte()
            safe_m_err = dis.readByte()
        }

        override fun change_ui() {
            runOnUiThread{
                if(is_socket_err_success() != true){
                    print_message_from_socket_err(applicationContext)
                }
                else if(is_safe_m_err_success() != true){
                    print_message_from_safe_m_err(applicationContext)
                }
                else{
                    Toast.makeText(applicationContext, "회원가입 성공", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

