package com.example.helper

import com.example.Constants
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class TrelloCall(val apiKey: String, val oauthToken: String) {
    val parameters = mutableMapOf<String, String>()
    var request = ""

    private var buildURL = ""

    /**
     * Map all the parameters from the mutable map to a string list of "key=value"
     * Then we separate them with the "&"
     */
    private fun formatParameters(): String {
        return parameters.map { (k, v) -> "$k=$v" }.joinToString("&")
    }

    /**
     * Build the URL we need to execute the HTTP request
     *
     * This URL contains all the [parameters], the [request] and some default values like the BaseURL, APIKey and OAuthToken
     */
    private fun build() {
        buildURL = "${Constants.TRELLO_BASEURL}$request?${formatParameters()}&key=$apiKey&token=$oauthToken"
    }

    /**
     * Get the URL of the HTTP request from [buildURL]
     *
     * If its empty we [build] the url first
     */
    fun getURL(): String {
        if (buildURL.isEmpty()) {
            build()
        }
        return buildURL
    }

    /**
     * Execute the HTTP request with the [buildURL]
     */
    suspend inline fun execute(client: HttpClient): String {
        return client.get(getURL())
    }
}