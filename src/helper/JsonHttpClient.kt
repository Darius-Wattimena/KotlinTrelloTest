package com.example

import io.ktor.client.HttpClient
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature

class JsonHttpClient {
    val client = HttpClient {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }
}