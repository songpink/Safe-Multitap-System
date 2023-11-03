package com.example.multitab_app

import android.app.Application

class global_var : Application() {
    var id_array = ByteArray(ID_MAX_LENGTH)
    var login_token_array = ByteArray(LOGIN_TOKEN_LENGTH)
    var selected_u_id_array = ByteArray(U_ID_LENGTH)
    var pw_array = ByteArray(PW_MAX_LENGTH)

    override fun onCreate() {
        super.onCreate()
        //여기에 코드 작성
    }

    fun init_id_array(){
        this.id_array.fill(0)
    }

    fun set_id_array(id_array :ByteArray){
        if(id_array.size <= ID_MAX_LENGTH){
            id_array.copyInto(this.id_array)
        }
    }

    fun get_id_array(): ByteArray {
       return this.id_array
    }

    fun init_login_token_array(){
        this.login_token_array.fill(0)
    }

    fun set_login_token_array(login_token_array :ByteArray){
        if(login_token_array.size <= LOGIN_TOKEN_LENGTH){
            login_token_array.copyInto(this.login_token_array)
        }
    }

    fun get_login_token_array(): ByteArray {
        return this.login_token_array
    }

    fun init_selected_u_id_array(){
        this.selected_u_id_array.fill(0)
    }

    fun set_selected_u_id_array(selected_u_id_array :ByteArray){
        if(selected_u_id_array.size <= U_ID_LENGTH){
            selected_u_id_array.copyInto(this.selected_u_id_array)
        }
    }

    fun get_selected_u_id_array(): ByteArray {
        return this.selected_u_id_array
    }

    fun init_pw_array(){
        this.pw_array.fill(0)
    }

    fun set_pw_array(arr :ByteArray){
        if(arr.size <= 8){
            arr.copyInto(this.pw_array)
        }
    }

    fun get_pw_array(): ByteArray {
        return this.pw_array
    }
}