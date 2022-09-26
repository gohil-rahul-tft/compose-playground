package com.example.composeplayground.network

import com.example.composeplayground.data.InteractiveMessageRequest
import com.example.composeplayground.data.PlainMessageRequest
import com.example.composeplayground.data.response.MessageResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface Api {

    @POST("conversation/read/")
    suspend fun sendPlainMessage(
        @Body data: PlainMessageRequest,
    ): MessageResponse


    @POST("conversation/read/")
    suspend fun sendInteractiveMessage(
        @Body data: InteractiveMessageRequest,
    ): MessageResponse

}