package com.example.request

import com.example.TrelloRequest
import com.example.helper.JsonHelper
import com.google.gson.Gson

abstract class BaseTrelloRequest<T> : TrelloRequest<T> {
    val client = JsonHelper.Client()
    val gson = Gson()
}