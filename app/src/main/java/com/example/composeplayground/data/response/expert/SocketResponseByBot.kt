package com.example.composeplayground.data.response.expert


import com.google.gson.annotations.SerializedName

data class SocketResponseByBot(
    @SerializedName("data")
    val data: Data,
    @SerializedName("type")
    val type: String
)