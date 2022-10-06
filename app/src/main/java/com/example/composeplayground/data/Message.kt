package com.example.composeplayground.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.composeplayground.data.response.Button
import java.util.*


// If sender ID == My ID then show message on right side
// If receiver ID == My ID then show message on left side

data class Message(
    val messageId: String = UUID.randomUUID().toString(),
    val senderId: Int = (1..2).random(),
    val receiverId: Int,
    val channelId: String? = null,
    val message: String,
    val buttons: List<Button>? = listOf(
        Button("1001", "Yes", "Yes", "Yes"),
        Button("1002", "No", "No", "No"),
    ),
)
