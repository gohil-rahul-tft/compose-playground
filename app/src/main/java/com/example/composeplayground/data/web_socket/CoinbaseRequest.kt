package com.example.composeplayground.data.web_socket


import com.google.gson.annotations.SerializedName

data class CoinbaseRequest(
    @SerializedName("type")
    val type: String = "subscribe",

    @SerializedName("channels")
    val channels: List<Channel> = listOf(
        Channel(name = "ticker", productIds = listOf("BTC-EUR"))
    )
)