package com.example.composeplayground.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

interface SafeApiCall {

    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Resource<T> {

        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(apiCall.invoke())
            } catch (exception: Exception) {

                when (exception) {
                    is HttpException -> {

                        /*val errorMessage =
                            JSONObject(exception.response()!!.errorBody()!!.charStream().readText())

                        Resource.Failure(
                            message = errorMessage.getString("message"),
                            errorCode = exception.code()
                        )*/


                        Resource.Failure(
                            message = exception.message.toString(),
                            errorCode = exception.code()
                        )

                    }

                    is IOException -> {
                        Resource.Failure(
                            errorCode = null,
                            isNetworkError = true,
                            message = exception.message
                        )
                    }

                    else -> Resource.Failure(
                        errorCode = null,
                        isNetworkError = true,
                        message = exception.message
                    )
                }

            }
        }
    }
}