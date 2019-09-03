package com.example

import com.example.request.BaseRequest
import com.example.trello.Response
import com.google.gson.Gson

class RequestExecuter {
    companion object {
        private val gson = Gson()

        suspend fun <T> execute(request: BaseRequest<T>): String {
            return try {
                val result = executeRequest(request)
                processResult(result)
            } catch (cause: Throwable) {
                val errorResponse = Response()
                errorResponse.error = cause.message
                gson.toJson(errorResponse)
            }
        }


        suspend fun <T> executeRequest(request: BaseRequest<T>): T {
            request.prepare()
            return request.execute()
        }

        fun <T> processResult(result: T): String {
            val response = Response()
            response.value = result
            return gson.toJson(response)
        }
    }
}