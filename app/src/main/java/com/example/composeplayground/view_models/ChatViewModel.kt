package com.example.composeplayground.view_models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeplayground.data.InteractiveMessageRequest
import com.example.composeplayground.data.Message
import com.example.composeplayground.data.PlainMessageRequest
import com.example.composeplayground.data.response.expert.SocketResponseToExpert
import com.example.composeplayground.network.Api
import com.example.composeplayground.network.MessageListener
import com.example.composeplayground.network.WebSocketManager
import com.example.composeplayground.utils.Constants
import com.example.composeplayground.utils.Resource
import com.example.composeplayground.utils.SafeApiCall
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
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


    private val gson by lazy { Gson() }

    val messageList = mutableStateListOf<Resource<Message>>()


    fun updateMessage(newValue: String) {
        message = newValue
    }


    fun <T> sendMessageToServer(data: T) = viewModelScope.launch {

        val response: Resource<Message> = when (data) {
            is PlainMessageRequest -> {

                messageList.add(Resource.Success(data.convertToMessage()))
                safeApiCall { api.sendPlainMessage(data).convertToMessage() }
            }

            is InteractiveMessageRequest -> {
                messageList.add(Resource.Success(data.convertToMessage()))
                safeApiCall { api.sendInteractiveMessage(data).convertToMessage() }
            }

            else -> safeApiCall {
                api.sendPlainMessage(data as PlainMessageRequest).convertToMessage()
            }   // Won't called
        }

        messageList.add(response)
    }


    /*----------------------------------- Web Socket --------------------------------*/


    fun connectSocket(socketUrl: String = Constants.SELF_BEST_SOCKET_URL) {
        // /chat/676/
        closeConnection()
        WebSocketManager.init(socketUrl, this)
        WebSocketManager.connect()
    }

    private fun closeConnection() {
        WebSocketManager.close()
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

        val response = gson.fromJson(text, SocketResponseToExpert::class.java)

        Log.d(TAG, "onMessage: $response")

        messageList.add(Resource.Success(response.data.convertToMessage()))

    }

}


