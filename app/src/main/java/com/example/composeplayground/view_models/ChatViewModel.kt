package com.example.composeplayground.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeplayground.data.InteractiveMessageRequest
import com.example.composeplayground.data.Message
import com.example.composeplayground.data.PlainMessageRequest
import com.example.composeplayground.network.Api
import com.example.composeplayground.utils.Constants
import com.example.composeplayground.utils.Resource
import com.example.composeplayground.utils.SafeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val api: Api
) : ViewModel(), SafeApiCall {

    var message by mutableStateOf("")
        private set

    /*private var _messageList = MutableStateFlow<MutableList<Message>>(mutableListOf())
    val messageList = _messageList.asStateFlow()*/


    private var _messageList = MutableStateFlow<MutableList<Resource<Message>>>(mutableListOf())
    val messageList = _messageList.asStateFlow()


    init {
        viewModelScope.launch {
            /*repeat(2) {
                val list = mutableListOf<Message>()

                list.add(
                    Message(
                        senderId = Constants.USER_ID,
                        receiverId = Constants.BOT_ID,
                        message = "Launch!"
                    )
                )

                list.add(
                    Message(
                        senderId = Constants.BOT_ID,
                        receiverId = Constants.USER_ID,
                        message = "Are you stuck somewhere?"
                    )
                )


                _messageList.update {
                    it.toMutableList().apply {
                        addAll(list)
                    }
                }

                delay(500)
            }*/
        }

    }


    fun updateMessage(newValue: String) {
        message = newValue
    }


    fun sendMessage(message: Message) = viewModelScope.launch {

        var botMessage = Message(
            senderId = Constants.BOT_ID,
            receiverId = Constants.USER_ID,
            message = "Type your exact skills",
            buttons = emptyList()
        )

        if (message.message == "Hi".lowercase()) {
            botMessage = Message(
                senderId = Constants.BOT_ID,
                receiverId = Constants.USER_ID,
                message = "Are you stuck somewhere?",
            )
        }
        /*_messageList.update {
            it.toMutableList().apply {
                add(message)
            }
        }
        delay(1000)

        _messageList.update {
            it.toMutableList().apply {
                add(botMessage)
            }
        }*/
    }

    /*fun <T> sendMessageToServer(data: T) = viewModelScope.launch {

        val response: MessageResponse = when (data) {
            is PlainMessageRequest -> {

                _messageList.update {
                    it.toMutableList().apply {
                        add(data.convertToMessage())
                    }
                }
                api.sendPlainMessage(data)
            }

            is InteractiveMessageRequest -> {
                _messageList.update {
                    it.toMutableList().apply {
                        add(data.convertToMessage())
                    }
                }
                api.sendInteractiveMessage(data)
            }

            else -> api.sendPlainMessage(data as PlainMessageRequest)   // Won't called
        }

        _messageList.update {
            it.toMutableList().apply {
                add(response.convertToMessage())
            }
        }
    }*/


    fun <T> sendMessageToServer(data: T) = viewModelScope.launch {

        val response: Resource<Message> = when (data) {
            is PlainMessageRequest -> {

                _messageList.update {
                    it.toMutableList().apply {
                        add(Resource.Success(data.convertToMessage()))
                    }
                }
                safeApiCall { api.sendPlainMessage(data).convertToMessage() }
            }

            is InteractiveMessageRequest -> {
                _messageList.update {
                    it.toMutableList().apply {
                        add(Resource.Success(data.convertToMessage()))
                    }
                }
                safeApiCall { api.sendInteractiveMessage(data).convertToMessage() }
            }

            else -> safeApiCall {
                api.sendPlainMessage(data as PlainMessageRequest).convertToMessage()
            }   // Won't called
        }

        _messageList.update {
            it.toMutableList().apply {
                add(response)
            }
        }
    }
}


