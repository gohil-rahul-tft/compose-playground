package com.example.composeplayground.view_models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeplayground.data.InteractiveMessageRequest
import com.example.composeplayground.data.Message
import com.example.composeplayground.data.PlainMessageRequest
import com.example.composeplayground.data.response.expert.ExpertSocketResponse
import com.example.composeplayground.data.web_socket.CoinbaseRequest
import com.example.composeplayground.network.Api
import com.example.composeplayground.network.MessageListener
import com.example.composeplayground.network.WebSocketManager
import com.example.composeplayground.utils.Constants
import com.example.composeplayground.utils.Resource
import com.example.composeplayground.utils.SafeApiCall
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val api: Api
) : ViewModel(), SafeApiCall, MessageListener {

    companion object {
        private const val TAG = "ChatViewModel"
    }

    var message by mutableStateOf("")
        private set

    /*private var _messageList = MutableStateFlow<MutableList<Message>>(mutableListOf())
    val messageList = _messageList.asStateFlow()*/

    private val gson by lazy { Gson() }

    private var _messageList = MutableStateFlow<MutableList<Resource<Message>>>(mutableListOf())
    val messageList = _messageList.asStateFlow()


    fun updateMessage(newValue: String) {
        message = newValue
    }


    fun sendMessage(message: Message) = viewModelScope.launch {

        var botMessage = Message(
            senderId = Constants.BOT_ID,
            receiverId = Constants.USER_ID,
            message = "Type your exact skills",
            buttons = emptyList()
        )

        if (message.message == "Hi".lowercase()) {
            botMessage = Message(
                senderId = Constants.BOT_ID,
                receiverId = Constants.USER_ID,
                message = "Are you stuck somewhere?",
            )
        }
        /*_messageList.update {
            it.toMutableList().apply {
                add(message)
            }
        }
        delay(1000)

        _messageList.update {
            it.toMutableList().apply {
                add(botMessage)
            }
        }*/
    }

    /*fun <T> sendMessageToServer(data: T) = viewModelScope.launch {

        val response: MessageResponse = when (data) {
            is PlainMessageRequest -> {

                _messageList.update {
                    it.toMutableList().apply {
                        add(data.convertToMessage())
                    }
                }
                api.sendPlainMessage(data)
            }

            is InteractiveMessageRequest -> {
                _messageList.update {
                    it.toMutableList().apply {
                        add(data.convertToMessage())
                    }
                }
                api.sendInteractiveMessage(data)
            }

            else -> api.sendPlainMessage(data as PlainMessageRequest)   // Won't called
        }

        _messageList.update {
            it.toMutableList().apply {
                add(response.convertToMessage())
            }
        }
    }*/


    fun <T> sendMessageToServer(data: T) = viewModelScope.launch {

        val response: Resource<Message> = when (data) {
            is PlainMessageRequest -> {

                _messageList.update {
                    it.toMutableList().apply {
                        add(Resource.Success(data.convertToMessage()))
                    }
                }
                safeApiCall { api.sendPlainMessage(data).convertToMessage() }
            }

            is InteractiveMessageRequest -> {
                _messageList.update {
                    it.toMutableList().apply {
                        add(Resource.Success(data.convertToMessage()))
                    }
                }
                safeApiCall { api.sendInteractiveMessage(data).convertToMessage() }
            }

            else -> safeApiCall {
                api.sendPlainMessage(data as PlainMessageRequest).convertToMessage()
            }   // Won't called
        }

        _messageList.update {
            it.toMutableList().apply {
                add(response)
            }
        }
    }


    /*----------------------------------- Web Socket --------------------------------*/

    /*init {
        connectSocket()
    }*/

    fun connectSocket(socketUrl: String = Constants.SELF_BEST_SOCKET_URL) {
        // /chat/676/
        closeConnection()
        WebSocketManager.init(socketUrl, this)
        WebSocketManager.connect()
    }

    private fun closeConnection() {
        WebSocketManager.close()
    }

    fun sendMessage(data: CoinbaseRequest) {
        val msg = gson.toJson(data)
        WebSocketManager.sendMessage(msg)

        /*if (WebSocketManager.sendMessage(msg)) {
            messageList.add(data)
        }*/
    }

    override fun onConnectSuccess() {
        Log.d(TAG, " Connected successfully \n ")
    }

    override fun onConnectFailed(message: String) {
        Log.d(TAG, " Connection failed \n ")
    }

    override fun onClose() {
        Log.d(TAG, " Closed successfully \n ")
    }

    override fun onMessage(text: String?) {

        Log.d(TAG, "onMessage: $text")
        val response = gson.fromJson(text, ExpertSocketResponse::class.java)

        Log.d(TAG, "onMessage: $response")

        _messageList.update {
            it.toMutableList().apply {
                add(Resource.Success(response.data.convertToMessage()))
            }
        }
    }

}


