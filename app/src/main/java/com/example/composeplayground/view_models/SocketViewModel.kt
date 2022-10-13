package com.example.composeplayground.view_models

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeplayground.data.SocketUpdate
import com.example.composeplayground.data.web_socket.CoinbaseRequest
import com.example.composeplayground.data.web_socket.CoinbaseResponse
import com.example.composeplayground.data.web_socket.CoinbaseWrapper
import com.example.composeplayground.network.web_socket.EasyWS
import com.example.composeplayground.network.web_socket.easyWebSocket
import com.example.composeplayground.utils.Constants
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class SocketViewModel @Inject constructor() : ViewModel() {
    companion object {
        private const val TAG = "SocketViewModel"
    }

    private val gson by lazy { Gson() }
    private val client by lazy { OkHttpClient() }
    private var easyWs: EasyWS? = null

    val newMessageList = mutableStateListOf<CoinbaseWrapper<CoinbaseResponse>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            connectSocket()
//            listenUpdates()
        }
    }

    private suspend fun connectSocket(socketUrl: String = Constants.COINBASE_URL) {
        easyWs = client.easyWebSocket(socketUrl)
    }

    private fun closeConnection() {
        Log.d(TAG, "closeConnection: CALLED")
//        WebSocketManager.close("Redirecting to different Screen!")
        easyWs?.webSocket?.close(1001, "Closing manually")
        client.dispatcher.executorService.shutdown()
    }

    fun sendMessage(data: CoinbaseRequest, message: String) =
        viewModelScope.launch(Dispatchers.IO) {

            if (message == "close".lowercase()) {
                newMessageList.add(CoinbaseWrapper.UserMessage(message = message))
                closeConnection()
                return@launch
            }
            val msg = gson.toJson(data)
            easyWs?.webSocket?.send(msg)
            newMessageList.add(CoinbaseWrapper.UserMessage(message = message))
            closeConnection()
        }


    private suspend fun listenUpdates() {

        easyWs?.textChannel?.consumeEach {
            when (it) {
                is SocketUpdate.Failure -> {
                    newMessageList.add(CoinbaseWrapper.Failure(message = it.exception?.message!!))
                }

                is SocketUpdate.Success -> {

                    val message = it.text
                    Log.d(TAG, "onMessage: $message")
                    val jsonObject = JSONObject(message)

                    if (jsonObject.getString("type") == "ticker") {
                        val response = gson.fromJson(message, CoinbaseResponse::class.java)

                        Log.d(TAG, "onMessage: $response")
                        newMessageList.add(CoinbaseWrapper.Response(response))
                    }

                }
            }
        }


    }

    fun receiveUpdates(text: String) {

        val message = text
        Log.d(TAG, "onMessage: $message")
        val jsonObject = JSONObject(message)

        if (jsonObject.getString("type") == "ticker") {
            val response = gson.fromJson(message, CoinbaseResponse::class.java)

            Log.d(TAG, "onMessage: $response")
            newMessageList.add(CoinbaseWrapper.Response(response))
        }

    }


    override fun onCleared() {
        super.onCleared()

        closeConnection()
    }

}