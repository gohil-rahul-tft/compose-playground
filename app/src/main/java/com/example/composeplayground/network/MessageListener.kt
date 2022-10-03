package com.example.composeplayground.network

interface MessageListener {
    fun  onConnectSuccess () // successfully connected
    fun  onConnectFailed (message: String) // connection failed
    fun  onClose () // close
    fun onMessage(text: String?)
}