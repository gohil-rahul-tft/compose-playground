package com.example.composeplayground.network.web_socket

import com.example.composeplayground.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient

private val pieSocketUrl =
    "wss://demo.piesocket.com/v3/channel_1?api_key=VCXCEuvhGcBDP7XhiJJUDvR1e1D3eiVjgZ9VRiaV&notify_self"
private val client by lazy { OkHttpClient() }

fun main() = runBlocking {
    println("main Start!")
    socketJob().join()

//    TestingChannelAndFlow()
//    client.dispatcher.executorService.shutdown()
    println("main Finished!")
}

private fun CoroutineScope.TestingChannelAndFlow() {
    val channel = MutableSharedFlow<String>()

    launch {
        /*for (num in channel) {
            delay(3000)
            println("[RECEIVED - 1] <-- $num")
        }*/
        channel.collect { num ->
            delay(3000)
            println("[RECEIVED - 1] <-- $num")
        }
    }
    launch {
        /*for (num in channel) {
            delay(1000)
            println("[RECEIVED - 2] <-- $num")
        }*/


        channel.collect { num ->
            delay(1000)
            println("[RECEIVED - 2] <-- $num")
        }
    }

    launch {
        repeat(10) {
            channel.emit(it.toString())
            delay(500)
        }
    }
}

private fun socketJob() = GlobalScope.launch {
    val easyWS = client.easyWebSocket(Constants.SOCKET_URL)
    println("[socketJob] Open: ${easyWS.response}")

    launch {
        val msg = "{\n" +
                "    \"type\": \"subscribe\",\n" +
                "    \"channels\": [{ \"name\": \"ticker\", \"product_ids\": [\"BTC-EUR\"] }]\n" +
                "}"

        easyWS.webSocket.send(msg)
    }
    /*launch {
        repeat(5) {
            easyWS.webSocket.send(it.toString())
            println("[socketJob] --> $it")
            delay(1000)
        }
    }*/

    launch {
        repeat(10) {
            delay(1000)
            if (it > 5) {
                easyWS.webSocket.close(1000, "Bye! [socketJob]")
                client.dispatcher.executorService.shutdown()
            }
        }
    }

    easyWS.textChannel.consumeEach { msg ->
        println("[socketJob] <-- $msg")
    }

    println("[socketJob] Finish!")
}