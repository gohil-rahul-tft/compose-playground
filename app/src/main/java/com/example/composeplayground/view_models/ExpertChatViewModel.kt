package com.example.composeplayground.view_models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeplayground.data.SocketUpdate
import com.example.composeplayground.data.response.expert_chat.ExpertChatRequest
import com.example.composeplayground.data.response.expert_chat.ExpertChatResponse
import com.example.composeplayground.network.web_socket.EasyWS
import com.example.composeplayground.network.web_socket.easyWebSocket
import com.example.composeplayground.utils.Constants
import com.example.composeplayground.utils.Resource
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
class ExpertChatViewModel @Inject constructor() : ViewModel() {

    companion object {
        private const val TAG = "ExpertChatViewModel"
    }

    private val gson by lazy { Gson() }
    private var easyWs: EasyWS? = null

    var message by mutableStateOf("")
        private set

    val messageList = mutableStateListOf<Resource<ExpertChatResponse>>()

    fun updateMessage(newValue: String) {
        message = newValue
    }


    init {
        viewModelScope.launch(Dispatchers.IO) {
//            listenUpdates()
        }
    }


    /*----------------------------------- Web Socket --------------------------------*/

    fun connectSocket(socketUrl: String = Constants.SELF_BEST_SOCKET_URL) =
        viewModelScope.launch(Dispatchers.IO) {
            // /chat/676/
            easyWs = OkHttpClient().easyWebSocket(socketUrl)
            Log.d(TAG, "connectSocket: Called Listen Channel")
            listenUpdates()
        }

    fun sendMessage(data: ExpertChatRequest) {
        val msg = gson.toJson(data)
        easyWs?.webSocket?.send(msg)
        messageList.add(Resource.Success(data.convertToExpertChatResponse()))
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


                    val jsonObject = JSONObject(text)
                    var responseObj = text!!

                    if (jsonObject.has("data")) {
                        responseObj = jsonObject.getString("data")
                    }

                    val response = gson.fromJson(responseObj, ExpertChatResponse::class.java)
                    Log.d(TAG, "onMessage: $response")

                    messageList.add(Resource.Success(response))

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


