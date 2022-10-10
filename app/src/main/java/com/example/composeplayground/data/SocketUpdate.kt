package com.example.composeplayground.data

import okio.ByteString

sealed class SocketUpdate {
    data class Success(
        val text: String? = null,
        val byteString: ByteString? = null,
    ) : SocketUpdate()

    data class Failure(
        val exception: Throwable? = null
    ) : SocketUpdate()
}