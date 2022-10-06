package com.example.composeplayground.view_models

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.composeplayground.data.web_socket.CoinbaseRequest
import com.example.composeplayground.data.web_socket.CoinbaseResponse
import com.example.composeplayground.network.MessageListener
import com.example.composeplayground.network.WebSocketManager
import com.example.composeplayground.utils.Constants
import com.example.composeplayground.utils.Resource
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SocketViewModel @Inject constructor() : ViewModel(), MessageListener {
    companion object {
        private const val TAG = "SocketViewModel"
    }

    private val gson by lazy { Gson() }

    val messageList = mutableStateListOf<CoinbaseResponse>()
    val newMessageList = mutableStateListOf<Resource<CoinbaseResponse>>()

    init {
        connectSocket()
    }

    private fun connectSocket(socketUrl: String = Constants.SELF_BEST_SOCKET_URL) {
        // /chat/667/
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
        newMessageList.add(Resource.Failure(message = message))
    }

    override fun onClose() {
        Log.d(TAG, " Closed successfully \n ")
    }

    override fun onMessage(text: String?) {

        Log.d(TAG, "onMessage: $text")
        val response = gson.fromJson(text, CoinbaseResponse::class.java)

        Log.d(TAG, "onMessage: $response")
        messageList.add(response)
        newMessageList.add(Resource.Success(response))

    }


    /*private fun connectToDynamicURL(){
        WebSocketManager2.init("").open{

        }.failure{

        }.close{

        }
    }*/
}