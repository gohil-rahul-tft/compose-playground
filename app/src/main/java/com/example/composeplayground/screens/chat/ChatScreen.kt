package com.example.composeplayground.screens.chat

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.composeplayground.data.InteractiveMessageRequest
import com.example.composeplayground.data.Message
import com.example.composeplayground.data.PlainMessageRequest
import com.example.composeplayground.navigation.ROUTE_CHAT
import com.example.composeplayground.navigation.ROUTE_EXPERT_CHAT
import com.example.composeplayground.screens.chat.components.ButtonGridLayout
import com.example.composeplayground.screens.chat.components.ChatBoxEditText
import com.example.composeplayground.screens.chat.components.ErrorMessage
import com.example.composeplayground.utils.Constants
import com.example.composeplayground.utils.Resource
import com.example.composeplayground.utils.createSocketUrl
import com.example.composeplayground.utils.toast
import com.example.composeplayground.view_models.ChatViewModel
import kotlinx.coroutines.launch

private const val TAG = "ChatScreen"

@Composable
fun ChatScreen(
    navController: NavController,
    senderId: String,
    receiverId: String,
    viewModel: ChatViewModel = hiltViewModel()
) {

    Log.d(
        TAG,
        "ChatScreen: SENDER - $senderId and RECEIVER - $receiverId"
    )

    LaunchedEffect(Unit) {
        viewModel.connectSocket(socketUrl = Constants.SELF_BEST_SOCKET_URL.createSocketUrl(senderId))
    }

    val messages = viewModel.messageList
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = false,
            state = listState
        ) {
            items(messages) { message ->

                when (message) {
                    is Resource.Failure -> {
                        Log.i(TAG, "ChatScreen: $message")
                        ErrorMessage(message.message.toString())
                    }

                    is Resource.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is Resource.Success -> {
                        LaunchedEffect(Unit) {
                            listState.animateScrollToItem(index = messages.lastIndex)
                        }

                        if (!message.value.channelId.isNullOrEmpty()) {
                            LaunchedEffect(Unit) {
                                context.toast(message.value.message)

                                navController.navigate(
                                    "$ROUTE_EXPERT_CHAT?senderId=${senderId}&receiverId=${message.value.channelId}"
                                ) {
                                    popUpTo(ROUTE_CHAT) {
                                        inclusive = true
                                    }
                                }

                            }
                        } else {
                            MessageCard(
                                message = message.value,
                                senderId = senderId
                            ) { messageRequest ->
                                viewModel.sendMessageToServer(messageRequest)
                            }
                        }


                    }
                }

            }
        }

        ChatBoxEditText(
            message = viewModel.message,
            onChange = { viewModel.updateMessage(it) },
            onSend = {
                if (it.isEmpty()) {
                    context.toast("Please Enter Message!")
                    return@ChatBoxEditText
                }

                val messageObj = buildPlainMessage(message = it, senderId)
                viewModel.sendMessageToServer(messageObj)
                viewModel.updateMessage("")

                scope.launch {
                    if (messages.isNotEmpty())
                        listState.animateScrollToItem(index = messages.lastIndex)
                }

            },
        )
    }
}


private fun buildPlainMessage(message: String, senderId: String): PlainMessageRequest {
    return PlainMessageRequest(
        senderId = senderId,
        message = message,
    )
}

private fun buildInteractiveMessage(
    message: String,
    eventName: String,
    senderId: String
): InteractiveMessageRequest {
    return InteractiveMessageRequest(
        senderId = senderId,
        message = message,
        eventName = eventName
    )
}


@Composable
fun MessageCard(
    message: Message? = null,
    senderId: String? = null,
    onSend: (messageRequest: InteractiveMessageRequest) -> Unit
) {

    message!!   // To Avoid preview conflict
    senderId!!   // To Avoid preview conflict

    var isEnabled by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = if (message.senderId == senderId)
            Alignment.End
        else
            Alignment.Start
    ) {

        if (message.message.isNotEmpty()) {
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
        }

        Text(
            modifier = Modifier.padding(8.dp),
            fontSize = 12.sp,
            color = MaterialTheme.colors.onSurface,
            text = if (message.senderId == Constants.USER_ID)
                "You"
            else
                "Bot"
        )

        if (!message.buttons.isNullOrEmpty()) {

            ButtonGridLayout(
                buttons = message.buttons,
                isEnabled = isEnabled
            ) { button ->

                isEnabled = false

                val messageObj = buildInteractiveMessage(
                    eventName = button.id,
                    message = button.value,
                    senderId = senderId
                )

                onSend.invoke(messageObj)

            }

        }

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
        message = Message(
            senderId = Constants.BOT_ID,
            receiverId = Constants.USER_ID,
            message = "Hello"
        )
    ) {}
}



