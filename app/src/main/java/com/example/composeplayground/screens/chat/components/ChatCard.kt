package com.example.composeplayground.screens.chat.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeplayground.data.Message
import com.example.composeplayground.data.response.expert_chat.ExpertChatRequest
import com.example.composeplayground.data.response.expert_chat.ExpertChatResponse
import com.example.composeplayground.utils.Constants


@Composable
fun MessageCard(
    message: ExpertChatResponse? = null,
    senderId: String? = null,
) {

    message!!   // To Avoid preview conflict
    senderId!!   // To Avoid preview conflict


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = if (message.senderId == senderId)
            Alignment.End
        else
            Alignment.Start
    ) {

        Card(
            modifier = Modifier.widthIn(max = 340.dp),
            shape = cardShapeFor(message.senderId == senderId),
            backgroundColor = if (message.senderId == senderId)
                MaterialTheme.colors.primary
            else
                MaterialTheme.colors.secondary
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = message.message,
                style = MaterialTheme.typography.body2,
                color = if (message.senderId == senderId)
                    MaterialTheme.colors.onPrimary
                else
                    MaterialTheme.colors.onSecondary
            )
        }


        Text(
            modifier = Modifier.padding(8.dp),
            fontSize = 12.sp,
            color = MaterialTheme.colors.onSurface,
            text = if (message.senderId == senderId)
                "You"
            else
                "..."
        )
    }
}

@Composable
fun cardShapeFor(isMine: Boolean = true): Shape {
    val roundedCorners = RoundedCornerShape(16.dp)
    return when {
        isMine -> roundedCorners.copy(bottomEnd = CornerSize(0))
        else -> roundedCorners.copy(bottomStart = CornerSize(0))
    }
}


@Composable
@Preview(showBackground = true)
fun MessageCardPreview() {
    MessageCard(
        message = ExpertChatResponse(
            senderId = "676",
            receiverId = "776",
            message = "Hello"
        )
    )
}
