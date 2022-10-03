package com.example.composeplayground.data.web_socket


import com.google.gson.annotations.SerializedName

data class Channel(
    @SerializedName("name")
    val name: String,

    @SerializedName("product_ids")
    val productIds: List<String>
)