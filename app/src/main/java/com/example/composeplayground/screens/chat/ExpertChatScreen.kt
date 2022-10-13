package com.example.composeplayground.screens.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import com.example.composeplayground.data.response.expert_chat.ExpertChatRequest
import com.example.composeplayground.screens.chat.components.ChatBoxEditText
import com.example.composeplayground.screens.chat.components.ErrorMessage
import com.example.composeplayground.screens.chat.components.MessageCard
import com.example.composeplayground.utils.Constants
import com.example.composeplayground.utils.Resource
import com.example.composeplayground.utils.createSocketUrl
import com.example.composeplayground.utils.normalText
import com.example.composeplayground.utils.toast
import com.example.composeplayground.view_models.ExpertChatViewModel
import kotlinx.coroutines.launch

private const val TAG = "ExpertChatScreen"

@Composable
fun ExpertChatScreen(
    navController: NavController,
    senderId: String,
    receiverId: String,
    viewModel: ExpertChatViewModel = hiltViewModel()
) {
    val messages = viewModel.messageList
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    
    Log.d(
        TAG,
        "ExpertChatScreen: SENDER - $senderId and RECEIVER - $receiverId"
    )

    LaunchedEffect(Unit) {
        /*viewModel.connectSocket(
            socketUrl = Constants.SELF_BEST_SOCKET_URL.createSocketUrl(
                senderId
            )
        )*/

        // on below line we are creating a new broad cast manager.
        val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            // we will receive data updates in onReceive method.
            override fun onReceive(context: Context?, intent: Intent) {
                // Get extra data included in the Intent
                val message = intent.getStringExtra("message")
                // on below line we are updating the data in our text view.

                Log.d(TAG, "onReceive: $message")
                viewModel.receiveUpdates(message!!)
            }
        }


        // on below line we are registering our local broadcast manager.
        LocalBroadcastManager.getInstance(context).registerReceiver(
            broadcastReceiver, IntentFilter(Constants.EXPERT_CHAT_ACTION)
        )
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = false,
            state = listState
        ) {
            items(items = messages) { message ->

                when (message) {

                    is Resource.Success -> {
                        LaunchedEffect(Unit) {
                            listState.animateScrollToItem(index = messages.lastIndex)
                        }

                        MessageCard(
                            message = message.value,
                            senderId = senderId
                        )

                    }

                    is Resource.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            CircularProgressIndicator()
                        }
                    }


                    is Resource.Failure -> {
                        Log.i(TAG, "ChatScreen: $message")
                        ErrorMessage(message.message.toString())
                    }


                }

            }
        }

        ChatBoxEditText(
            message = viewModel.message,
            onChange = { viewModel.updateMessage(it) },
            onSend = {
                if (it.normalText().isEmpty()) {
                    context.toast("Please Enter Message!")
                    return@ChatBoxEditText
                }

                val messageObj = buildMessage(
                    message = it,
                    senderId = senderId,
                    receiverId = receiverId
                )
                viewModel.sendMessage(messageObj)
                viewModel.updateMessage("")

                scope.launch {

                    // todo bug automatically scroll when pin to screen
                    if (messages.isNotEmpty())
                        listState.animateScrollToItem(index = messages.lastIndex)
                }

            },
        )
    }
}


private fun buildMessage(message: String, senderId: String, receiverId: String): ExpertChatRequest {
    return ExpertChatRequest(
        senderId = senderId,
        receiverId = receiverId,
        message = message,
    )
}



