package com.example.multitab_app

import java.io.ByteArrayOutputStream

const val ID_MAX_LENGTH = 8
const val ID_MIN_LENGTH = 5

const val PW_MAX_LENGTH = 16
const val PW_MIN_LENGTH = 8

const val U_ID_LENGTH = 8
const val LOGIN_TOKEN_LENGTH = 8

const val LOGIN_MESSAGE_TYPE = '3'.code.toByte()
const val REGISTER_MESSAGE_TYPE = '2'.code.toByte()
const val GET_POWER_LIST_MESSAGE_TYPE = '5'.code.toByte()
const val SET_RELAY_REQ_MESSAGE_TYPE = '6'.code.toByte()
const val ADD_POWER_MESSAGE_TYPE = 'A'.code.toByte()
const val GET_RELAY_REQ_MESSAGE_TYPE = '9'.code.toByte()
const val REQUEST_LOG_MESSAGE_TYPE = 'D'.code.toByte()


const val SAFE_M_SUCCESS = '0'.code.toByte()
const val SAFE_M_DUPLICATED_ID = '1'.code.toByte()
const val SAFE_M_INVALID_ID_PW = '2'.code.toByte()
const val SAFE_M_ID_NOT_EXISTS = '3'.code.toByte()
const val SAFE_M_INVALID_TOKEN = '4'.code.toByte()
const val SAFE_M_POWER_NOT_EXISTS = '5'.code.toByte()
const val SAFE_M_USER_NO_OWN_TO_POWER = '6'.code.toByte()
const val SAFE_M_SQL_FAIL = '9'.code.toByte()

const val SOCKET_SUCCESS = 0
const val SOCKET_UNKNOWN_ERROR = 1
const val SOCKET_READ_TIMEOUT = 2
const val SOCKET_UNKNOWN_SERVER = 3

const val SOCKET_TIME_FOR_READ = 15000

const val WIFI_SSID_MAX_LENGTH = 63
const val WIFI_PW_MAX_LENGTH = 63

const val ID_PATTERN = ""

const val PY_HOST = "116.43.67.165"
const val C_HOST = "15.164.85.107"
const val ESP_HOST = "192.168.2.1"
const val PORT = 16000

const val ID_ARRAY_FOR_SEND_NAME = "id_arr"
const val PW_ARRAY_FOR_SEND_NAME = "pw_arr"
const val LOGIN_TOKEN_ARRAY_FOR_SEND_NAME = "session_id"
const val JPG_ARRAY_NAME = "jpg_arr"
const val SELECTED_U_ID_ARRAY_NAME = "selected_u_id_arr"