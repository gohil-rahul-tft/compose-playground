package com.example.composeplayground.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Locale

private const val TAG = "Extension"

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun String.normalText(): String {
    return this.trim()
}


fun Int.translateErrorCode(): String {
    return when (this) {
        404 -> "Route Not Found"
        401 -> "Invalid Session"
        500 -> "Internal Server Error"
        else -> "Something went wrong!"
    }
}


fun String.formatDateTime(): String =

    try {
        val dateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

        val date =
            dateFormat.parse(this) //You will get date object relative to server/client timezone wherever it is parsed

        val formatter = SimpleDateFormat(
            "dd-MM-yyyy hh:mm:ss a",
            Locale.getDefault()
        ) //If you need time just put specific format for time like 'HH:mm:ss'

        formatter.format(date ?: "")

    } catch (e: Exception) {
        Log.d(TAG, ":${e.message} ")
        ""
    }