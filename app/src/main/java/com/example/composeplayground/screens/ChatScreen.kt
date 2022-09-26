package com.example.composeplayground.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composeplayground.data.InteractiveMessageRequest
import com.example.composeplayground.data.Message
import com.example.composeplayground.data.PlainMessageRequest
import com.example.composeplayground.data.response.Button
import com.example.composeplayground.utils.Constants
import com.example.composeplayground.utils.Resource
import com.example.composeplayground.utils.toast
import com.example.composeplayground.view_models.ChatViewModel
import kotlinx.coroutines.launch

private const val TAG = "ChatScreen"

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {

    val messages = viewModel.messageList.collectAsState()
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
            items(messages.value) { message ->

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
                            listState.animateScrollToItem(index = messages.value.lastIndex)
                        }
                        MessageCard(message.value) { messageRequest ->
                            viewModel.sendMessageToServer(messageRequest)
                        }
                    }
                }

            }
        }

        ChatBoxEditText(
            viewModel = viewModel,
            onChange = { viewModel.updateMessage(it) },
            onSend = {
                if (it.isEmpty()) {
                    context.toast("Please Enter Message!")
                    return@ChatBoxEditText
                }

                val messageObj = buildPlainMessage(it)
                viewModel.sendMessageToServer(messageObj)
                viewModel.updateMessage("")

                scope.launch {
                    if (messages.value.isNotEmpty())
                        listState.animateScrollToItem(index = messages.value.lastIndex)
                }

            }
        )
    }
}

private fun buildMessage(message: String): Message {
    return Message(
        senderId = Constants.USER_ID,
        receiverId = Constants.BOT_ID,
        message = message,
        buttons = emptyList()
    )
}

private fun buildPlainMessage(message: String): PlainMessageRequest {
    return PlainMessageRequest(
        senderId = Constants.USER_ID,
        message = message,
    )
}

private fun buildInteractiveMessage(message: String, eventName: String): InteractiveMessageRequest {
    return InteractiveMessageRequest(
        senderId = Constants.USER_ID,
        message = message,
        eventName = eventName
    )
}


@Composable
@Preview(showBackground = true)
fun ChatScreenPreview() {
    ChatScreen(viewModel = hiltViewModel())
}


@Composable
fun ChatBoxEditText(
    viewModel: ChatViewModel,
    onChange: (message: String) -> Unit,
    onSend: (message: String) -> Unit,
) {
    val message = viewModel.message
    Row {
        TextField(
            value = message,
            onValueChange = { onChange(it) },
            modifier = Modifier.weight(1f),
            placeholder = { Text(text = "Type your message...") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions { onSend(message) },
        )

        Button(
            onClick = { onSend(message) },
            enabled = true,
            modifier = Modifier.height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
fun ChatBoxEditTextPreview() {
    ChatBoxEditText(hiltViewModel(), {}, {})
}


@Composable
fun MessageCard(
    message: Message? = null,
    onSend: (messageRequest: InteractiveMessageRequest) -> Unit
) {

    message!!   // To Avoid preview conflict

    var isEnabled by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = if (message.senderId == Constants.USER_ID)
            Alignment.End
        else
            Alignment.Start
    ) {

        Card(
            modifier = Modifier.widthIn(max = 340.dp),
            shape = cardShapeFor(message.senderId == Constants.USER_ID),
            backgroundColor = if (message.senderId == Constants.USER_ID)
                MaterialTheme.colors.primary
            else
                MaterialTheme.colors.secondary
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = message.message,
                color = if (message.senderId == Constants.USER_ID)
                    MaterialTheme.colors.onPrimary
                else
                    MaterialTheme.colors.onSecondary
            )
        }


        Text(
            modifier = Modifier.padding(8.dp),
            fontSize = 12.sp,
            text = if (message.senderId == Constants.USER_ID)
                "You"
            else
                "Bot"
        )

        if (message.buttons.isNotEmpty()) {

            ButtonGridLayout(
                buttons = message.buttons,
                isEnabled = isEnabled
            ) { button ->

                isEnabled = false

                val messageObj = buildInteractiveMessage(
                    eventName = button.id,
                    message = button.value,
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
        ), {})
}


@Composable
fun ButtonGridLayout(
    buttons: List<Button>,
    isEnabled: Boolean = true,
    onClick: (button: Button) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        buttons.forEach { button ->
            OutlinedButton(
                modifier = Modifier.wrapContentSize().padding(8.dp),
                onClick = { onClick(button) },
                enabled = isEnabled,
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, MaterialTheme.colors.secondary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.onSecondary)

            ) {
                Text(text = button.value, modifier = Modifier.padding(8.dp))
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun ButtonGridLayoutPreview() {
    ButtonGridLayout(
        buttons = listOf(
            Button("1001", "Yes", "Yes", "Instant Expert Help"),
            Button("1001", "Yes", "Yes", "Online Resources"),
            Button("1001", "Yes", "Yes", "Exit Chat"),
        ),
        onClick = {}
    )
}


@Composable
fun ErrorMessage(message: String = "Error Message") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {

        Text(
            text = message,
            color = Color.Red,
            style = MaterialTheme.typography.subtitle2,
            fontWeight = FontWeight.Bold
        )

    }
}


@Composable
@Preview(showBackground = true)
fun ErrorMessagePreview() {
    ErrorMessage()
}