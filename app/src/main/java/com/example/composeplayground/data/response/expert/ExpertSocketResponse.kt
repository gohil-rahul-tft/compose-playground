package com.example.composeplayground.data.response.expert


import com.google.gson.annotations.SerializedName

data class ExpertSocketResponse(
    @SerializedName("data")
    val data: Data,
    @SerializedName("type")
    val type: String
)