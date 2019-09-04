package com.example.request

import com.example.JsonHttpClient
import com.example.TrelloRequest
import com.google.gson.Gson

abstract class BaseTrelloRequest<T> : TrelloRequest<T> {
    val client = JsonHttpClient().client
    val gson = Gson()
}