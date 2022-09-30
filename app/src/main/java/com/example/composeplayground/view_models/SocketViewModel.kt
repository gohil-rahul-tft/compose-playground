package com.example.composeplayground.view_models

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.composeplayground.network.MessageListener
import com.example.composeplayground.network.WebSocketManager
import com.example.composeplayground.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SocketViewModel @Inject constructor() : ViewModel(), MessageListener {

    val messageList = mutableStateListOf<String>()

    init {
        WebSocketManager.init(Constants.SOCKET_URL, this)
    }

    fun connectSocket() {
        WebSocketManager.connect()
    }

    private fun closeConnection() {
        WebSocketManager.close()
    }

    fun sendMessage(data: String) {
        if (WebSocketManager.sendMessage(data)) {
            messageList.add(data)
        }
    }

    override fun onConnectSuccess() {
        messageList.add(" Connected successfully \n ")
    }

    override fun onConnectFailed() {
        messageList.add(" Connection failed \n ")
    }

    override fun onClose() {
        messageList.add(" Closed successfully \n ")
    }

    override fun onMessage(text: String?) {
        messageList.add(" Receive message: $text \n ")
    }

}