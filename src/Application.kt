package com.example

import com.example.request.GetActions
import com.example.request.GetBoard
import com.example.request.GetBoardStatistics
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
                    get("/processedBoard/{id}") {
                        val id = call.parameters["id"]
                        call.respondText(getBoardsAndProcessActionsOfAllCards(id), contentType = ContentType.Application.Json)
                    }

                    get("/board/{id}/statistics") {
                        val id = call.parameters["id"]
                        call.respondText(getBoardStatistics(id), contentType = ContentType.Application.Json)
                    }

                    route("/testBoard") {
                        get {
                            call.respondText(getBoardsAndProcessActionsOfAllCards("RsU5w4Bn"), contentType = ContentType.Application.Json)
                        }
                    }
                }
            }
        }
    }
}

suspend fun getBoardStatistics(id: String?): String {
    return RequestExecuter().execute(GetBoardStatistics(id))
}

suspend fun getBoardsAndProcessActionsOfAllCards(id: String?): String {
    val gson = Gson()
    return try {
        val request = GetBoard(id)
        request.prepare()
        val board = request.execute()

        for (list in board.lists)
            for (card in list.cards) {
                val actionRequest = GetActions(card.id)
                actionRequest.prepare()
                card.actions = actionRequest.execute()
            }

        val response = Response()
        response.value = board
        gson.toJson(response)
    } catch (cause: Throwable) {
        val errorResponse = Response()
        errorResponse.error = cause.message
        gson.toJson(errorResponse)
    }
}

suspend fun getBoards(id: String?): String {
    return RequestExecuter().execute(GetBoard(id))
}
