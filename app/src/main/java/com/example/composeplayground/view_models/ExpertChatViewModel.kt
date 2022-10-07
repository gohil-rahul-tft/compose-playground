package com.example.composeplayground.view_models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.composeplayground.data.response.expert_chat.ExpertChatRequest
import com.example.composeplayground.data.response.expert_chat.ExpertChatResponse
import com.example.composeplayground.network.MessageListener
import com.example.composeplayground.network.WebSocketManager
import com.example.composeplayground.utils.Constants
import com.example.composeplayground.utils.Resource
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject


@HiltViewModel
class ExpertChatViewModel @Inject constructor() : ViewModel(), MessageListener {

    companion object {
        private const val TAG = "ExpertChatViewModel"
    }

    private val gson by lazy { Gson() }

    var message by mutableStateOf("")
        private set

    val messageList = mutableStateListOf<Resource<ExpertChatResponse>>()

    fun updateMessage(newValue: String) {
        message = newValue
    }


    /*----------------------------------- Web Socket --------------------------------*/

    fun connectSocket(socketUrl: String = Constants.SELF_BEST_SOCKET_URL) {
        // /chat/676/
        closeConnection()
        WebSocketManager.init(socketUrl, this)
        WebSocketManager.connect()
    }

    fun sendMessage(data: ExpertChatRequest) {
        val msg = gson.toJson(data)
        WebSocketManager.sendMessage(msg)
        messageList.add(Resource.Success(data.convertToExpertChatResponse()))
    }

    override fun onMessage(text: String?) {

        Log.d(TAG, "onMessage: $text")

        try {

            val jsonObject = JSONObject(text)
            var responseObj = text!!

            if (jsonObject.has("data")) {
                responseObj = jsonObject.getString("data")
            }

            val response = gson.fromJson(responseObj, ExpertChatResponse::class.java)
            Log.d(TAG, "onMessage: $response")

            messageList.add(Resource.Success(response))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    private fun closeConnection() {
        WebSocketManager.close()
        Log.d(TAG, "closeConnection: CONNECTION CLOSED!")
    }


    override fun onConnectSuccess() {
        Log.d(TAG, " Connected successfully \n ")
    }

    override fun onConnectFailed(message: String) {
        Log.d(TAG, " Connection failed $message\n ")
        messageList.add(Resource.Failure(message = message))
    }

    override fun onClose() {
        Log.d(TAG, " Closed successfully \n ")
    }


}


