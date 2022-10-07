package com.example.composeplayground.data.response.expert_chat


import com.example.composeplayground.data.Message
import com.example.composeplayground.utils.Constants
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
) {
    fun convertToExpertChatResponse() = ExpertChatResponse(
        senderId = senderId,
        receiverId = receiverId,
        message = message,
    )
}