package com.example.composeplayground.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.composeplayground.data.SocketUpdate
import com.example.composeplayground.network.web_socket.EasyWS
import com.example.composeplayground.network.web_socket.easyWebSocket
import com.example.composeplayground.utils.Constants
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient

class CoinbaseService : Service() {

    private val client by lazy { OkHttpClient() }
    private var easyWs: EasyWS? = null
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val context: Context = this

    companion object {
        private const val TAG = "CoinbaseService"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "onCreate: CALLED FROM SERVICE")

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(TAG, "onStartCommand: CALLED FROM SERVICE")

        scope.launch {
            easyWs = client.easyWebSocket(Constants.COINBASE_URL)
            socketJob()
        }

        return START_STICKY
    }


    private suspend fun socketJob() = withContext(Dispatchers.IO) {
        try {
//            easyWs = client.easyWebSocket(Constants.COINBASE_URL)
            Log.d(TAG, "[socketJob] Open: ${easyWs?.response}")

            launch {
                val msg = "{\n" +
                        "    \"type\": \"subscribe\",\n" +
                        "    \"channels\": [{ \"name\": \"ticker\", \"product_ids\": [\"BTC-EUR\"] }]\n" +
                        "}"

                easyWs?.webSocket?.send(msg)
            }

            launch {
                repeat(10) {
                    delay(1000)
                    if (it > 5) {
                        /*job.cancel()
                        easyWS.webSocket.close(1000, "Bye! [socketJob]")
                        client.dispatcher.executorService.shutdown()*/
                    }
                }
            }

            easyWs?.textChannel?.consumeEach { msg ->
                when (msg) {
                    is SocketUpdate.Failure -> Log.d(TAG, "[socketJob] <-- ${msg.exception}")
                    is SocketUpdate.Success -> {
                        Log.d(TAG, "[socketJob] <-- ${msg.text}")

                        //  we are calling the intent with the action.
                        val intent = Intent("socket-action")
                        // on below line we are passing data to our broad cast receiver with key and value pair.
                        intent.putExtra("message", msg.text)
                        // on below line we are sending our broad cast with intent using broad cast manager.
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)

                    }
                }
            }

            Log.d(TAG, "[socketJob] Finish!")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "socketJob: ${e.message}")

            when (e) {
                is CancellationException -> {
                    easyWs?.webSocket?.close(1001, "Service Cancelled")
                    client.dispatcher.executorService.shutdown()
                }

                else -> {}
            }
//            stopSelf()
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "onDestroy: CALLED FROM SERVICE")
        easyWs?.webSocket?.close(1001, "Service Cancelled")
        client.dispatcher.executorService.shutdown()
        job.cancel()
    }
}