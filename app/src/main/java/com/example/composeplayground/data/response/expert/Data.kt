package com.example.composeplayground.data.response.expert


import com.example.composeplayground.data.Message
import com.example.composeplayground.data.response.Button
import com.example.composeplayground.utils.Constants
import com.google.gson.annotations.SerializedName

data class Data(

    // This should be HTML Message
    @SerializedName("message")
    val message: String,

    @SerializedName("sentBy")
    val sentBy: String,

    @SerializedName("sentTo")
    val sentTo: String?,

    @SerializedName("type")
    val type: String,

    @SerializedName("buttons")
    val buttons: List<Button>? = mutableListOf(),

    @SerializedName("timestamp")
    val timestamp: String?,

    @SerializedName("channel_id")
    val channelId: String?,
) {

    fun convertToMessage(): Message {
        return Message(
            channelId = channelId,
            message = message,
            buttons = buttons,
            senderId = sentBy,
            receiverId = sentTo ?: Constants.EXPERT_ID

        )
    }
}