package com.example.composeplayground.data.response


import com.example.composeplayground.data.Message
import com.example.composeplayground.utils.Constants
import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("buttons")
    val buttons: List<Button>,

    @SerializedName("message")
    val message: String,

    @SerializedName("sentBy")
    val sentBy: String,

    @SerializedName("timestamp")
    val timestamp: String
) {
    fun convertToMessage(): Message {
        return Message(
            senderId = Constants.BOT_ID,
            receiverId = Constants.USER_ID,
            message = message,
            buttons = buttons,
        )
    }
}