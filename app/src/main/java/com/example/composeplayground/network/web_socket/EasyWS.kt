package com.example.composeplayground.network.web_socket

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class EasyWS(val webSocket: WebSocket, val response: Response) {
    val textChannel = Channel<String>()
}

suspend fun OkHttpClient.easyWebSocket(url: String) = suspendCoroutine<EasyWS> {
    var easyWs: EasyWS? = null

    newWebSocket(Request.Builder().url(url).build(), object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            // println("onOpen: $response")
            easyWs = EasyWS(webSocket, response)        // DID Type Casting
            it.resume(easyWs!!)
        }


        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            it.resumeWithException(t)
            println("onFailure: $t $response")
        }


        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            // println("onClosing: $code $reason")
            webSocket.close(1000, "Bye!")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            runBlocking { easyWs!!.textChannel.send(text) }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)

            // println("<--[B] $bytes")
        }


        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            // println("onClosed: $code $reason")
            easyWs!!.textChannel.close()
        }

    })
}