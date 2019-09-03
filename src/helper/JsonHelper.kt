package com.example.helper

import com.example.TrelloCall
import com.google.gson.Gson
import io.ktor.client.HttpClient

class JsonHelper {
    companion object {
        suspend fun <T> fromJson(gson: Gson, call: TrelloCall, client: HttpClient, clazz: Class<T>): T {
            return gson.fromJson(call.execute(client), clazz)
        }
    }
}