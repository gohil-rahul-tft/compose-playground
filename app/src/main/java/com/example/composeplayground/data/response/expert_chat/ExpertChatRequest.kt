package com.example.composeplayground.data.response.expert_chat


import com.google.gson.annotations.SerializedName

data class ExpertChatRequest(
    @SerializedName("message")
    val message: String,
    @SerializedName("sentBy")
    val senderId: String,
    @SerializedName("sentTo")
    val receiverId: String,
    @SerializedName("type")
    val type: String = "chat"
)