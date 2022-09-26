package com.example.composeplayground.utils

import android.content.Context
import android.widget.Toast

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun String.normalText(): String {
    return this.trim()
}


fun Int.translateErrorCode(): String {
    return when (this) {
        404 -> "Route Not Found"
        500 -> "Internal Server Error"
        else -> "Something went wrong!"
    }
}

