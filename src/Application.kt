package com.example

import com.example.request.GetBoard
import com.example.trello.Response
import com.google.gson.Gson
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.method
import io.ktor.routing.route
import io.ktor.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val client = HttpClient(Apache) {
    }

    routing {
        method(HttpMethod.Get) {
            route("/") {
                route("json") {
                    get("/board/{id}") {
                        val id = call.parameters["id"]
                        call.respondText(getBoards(id), contentType = ContentType.Application.Json)
                    }
                    route("/testBoard") {
                        get {
                            call.respondText(getBoards("RsU5w4Bn"), contentType = ContentType.Application.Json)
                        }
                    }
                }
            }
        }
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }
    }
}

suspend fun getBoards(id: String?): String {
    //TODO find a better place to catch an error

    val gson = Gson()
    return try {
        val request = GetBoard(id)
        request.prepare()
        val response = Response()
        response.value = request.execute()
        gson.toJson(response)
    } catch (cause: Throwable) {
        val errorResponse = Response()
        errorResponse.error = cause.message
        gson.toJson(errorResponse)
    }
}
