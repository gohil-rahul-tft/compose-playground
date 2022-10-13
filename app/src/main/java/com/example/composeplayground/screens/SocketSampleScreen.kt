package com.example.composeplayground.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.composeplayground.data.web_socket.CoinbaseRequest
import com.example.composeplayground.data.web_socket.CoinbaseResponse
import com.example.composeplayground.data.web_socket.CoinbaseWrapper
import com.example.composeplayground.screens.chat.components.ChatBoxEditText
import com.example.composeplayground.screens.chat.components.ErrorMessage
import com.example.composeplayground.utils.formatDateTime
import com.example.composeplayground.view_models.SocketViewModel


private const val TAG = "SocketSampleScreen"

@Composable
fun SocketSampleScreen() {
    val viewModel: SocketViewModel = hiltViewModel()
    val messageList = viewModel.newMessageList
    var message by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
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
            broadcastReceiver, IntentFilter("socket-action")
        )
    }



    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {

        LazyColumn(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .weight(1f),
            state = scrollState
        ) {
            items(items = messageList) { message ->

                LaunchedEffect(messageList.size > 5) {
                    Log.d(TAG, "SocketSampleScreen: LAUNCHED EFFECT CALLED")
                    scrollState.animateScrollToItem(messageList.lastIndex)
                }

                when (message) {
                    is CoinbaseWrapper.Failure -> {
                        ErrorMessage(message = message.message)
                    }

                    is CoinbaseWrapper.Response -> {
                        StockPriceCard(data = message.value)
                    }

                    is CoinbaseWrapper.UserMessage -> {
                        CardSelfMessage(message = message.message)
                    }
                }

            }
        }


        ChatBoxEditText(
            message = message,
            onChange = { message = it },
            onSend = {
                viewModel.sendMessage(
                    data = buildMessage(),
                    message = message
                )
                message = ""
            }
        )
    }


    /*LaunchedEffect(messageList.size > 5) {
        Log.d(TAG, "SocketSampleScreen: LAUNCHED EFFECT CALLED")
        scrollState.animateScrollToItem(messageList.lastIndex)
    }*/
}

private fun buildMessage() = CoinbaseRequest()

@Composable
fun StockPriceCard(
    data: CoinbaseResponse?
) {
    data!!

    Card(
        modifier = Modifier
            .widthIn(max = 340.dp)
            .wrapContentHeight()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(4.dp)
            .copy(bottomStart = CornerSize(0)),
    ) {

        Column(
            modifier = Modifier
                .widthIn(340.dp)
                .background(MaterialTheme.colors.secondary)
                .padding(16.dp),
            horizontalAlignment = when (data.type) {
                "ticker" -> Alignment.Start
                else -> Alignment.End
            },
        ) {

            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = data.productId,
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )


                Text(
                    text = "Price : ${data.price}",
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold,
                )


            }
            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = data.time.formatDateTime(),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSecondary,
            )

        }
    }
}


@Composable
@Preview(showBackground = true)
fun StockPriceCardPreview() {
    StockPriceCard(null)
}


@Composable
fun CardSelfMessage(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.End

    ) {

        Card(
            modifier = Modifier.widthIn(max = 340.dp),
            shape = RoundedCornerShape(4.dp).copy(bottomEnd = CornerSize(0)),
            backgroundColor = MaterialTheme.colors.primary
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = message,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onPrimary
            )
        }

    }
}