package com.example.composeplayground.data


import com.example.composeplayground.utils.Constants
import com.example.composeplayground.utils.EventType
import com.google.gson.annotations.SerializedName

data class PlainMessageRequest(
    @SerializedName("sender_id")
    val senderId: Int = Constants.USER_ID,

    @SerializedName("event_message")
    val message: String,

    @SerializedName("event_type")
    val eventType: String = EventType.MESSAGE.value,
) {
    fun convertToMessage(): Message {
        return Message(
            senderId = Constants.USER_ID,
            receiverId = Constants.BOT_ID,
            message = message,
            buttons = emptyList(),
        )
    }
}