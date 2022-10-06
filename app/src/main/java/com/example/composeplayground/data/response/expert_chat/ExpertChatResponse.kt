package com.example.composeplayground.data.response.expert_chat


import com.example.composeplayground.utils.Constants
import com.google.gson.annotations.SerializedName

data class ExpertChatResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("sentBy")
    val senderId: String,
    @SerializedName("sentTo")
    val receiverId: String,
    @SerializedName("type")
    val type: String = Constants.CHAT
)