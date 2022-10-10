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
import com.example.composeplayground.data.SocketUpdate
import com.example.composeplayground.data.response.expert.SocketResponseByBot
import com.example.composeplayground.network.Api
import com.example.composeplayground.network.web_socket.EasyWS
import com.example.composeplayground.network.web_socket.easyWebSocket
import com.example.composeplayground.utils.Constants
import com.example.composeplayground.utils.Resource
import com.example.composeplayground.utils.SafeApiCall
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val api: Api
) : ViewModel(), SafeApiCall {

    companion object {
        private const val TAG = "ChatViewModel"
    }

    var message by mutableStateOf("")
        private set


    private val gson by lazy { Gson() }
    private var easyWs: EasyWS? = null


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
            }   // Will never called
        }

        messageList.add(response)
    }


    /*----------------------------------- Web Socket --------------------------------*/

    init {
        viewModelScope.launch(Dispatchers.IO) {
            listenUpdates()
        }
    }

    fun connectSocket(socketUrl: String = Constants.SELF_BEST_SOCKET_URL) =
        viewModelScope.launch(Dispatchers.IO) {
            // /chat/676/
            easyWs = OkHttpClient().easyWebSocket(socketUrl)
        }


    private suspend fun listenUpdates() {

        easyWs?.textChannel?.consumeEach {
            when (it) {
                is SocketUpdate.Failure -> {
                    messageList.add(Resource.Failure(message = it.exception?.message!!))
                }

                is SocketUpdate.Success -> {

                    val text = it.text
                    Log.d(TAG, "onMessage: $text")

                    val response = gson.fromJson(text, SocketResponseByBot::class.java)

                    Log.d(TAG, "onMessage: $response")

                    messageList.add(Resource.Success(response.data.convertToMessage()))

                }
            }
        }


    }


    private fun closeConnection() {
        easyWs?.webSocket?.close(1001, "Closing manually")
        Log.d(TAG, "closeConnection: CONNECTION CLOSED!")
    }

    override fun onCleared() {
        super.onCleared()

        closeConnection()
    }

}


