package com.example.request

import com.example.JsonHttpClient
import com.example.Request
import com.google.gson.Gson

abstract class BaseRequest<T> : Request<T> {
    val client = JsonHttpClient().client
    val gson = Gson()
}