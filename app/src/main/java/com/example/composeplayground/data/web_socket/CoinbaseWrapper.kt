package com.example.composeplayground.data.web_socket

sealed class CoinbaseWrapper<out T> {
    data class UserMessage<out T>(val message: String) : CoinbaseWrapper<T>()
    data class Response<out T>(val value: T) : CoinbaseWrapper<T>()
    data class Failure<out T>(val message: String) : CoinbaseWrapper<T>()
}
