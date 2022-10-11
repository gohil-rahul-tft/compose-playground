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
    val timestamp: String,

    @SerializedName("channel_id")
    val channelId: String?,
) {
    fun convertToMessage() = Message(
        senderId = Constants.BOT_ID,
        receiverId = Constants.USER_ID, // todo this should be received from shared pref.
        message = message,
        buttons = buttons,
        channelId = channelId,
    )
}