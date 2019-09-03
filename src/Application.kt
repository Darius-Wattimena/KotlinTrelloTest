package com.example

import com.example.request.*
import com.example.trello.Response
import com.google.gson.Gson
import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
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
import io.ktor.util.pipeline.PipelineContext

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

                    /**
                     * Board
                     */

                    get("/board/{id}") {
                        val id = call.parameters["id"]
                        call.respondText(RequestExecuter.execute(GetBoard(id)), contentType = ContentType.Application.Json)
                    }

                    get("/board/{id}/detailed") {
                        val id = call.parameters["id"]
                        call.respondText(RequestExecuter.execute(GetDetailedBoard(id)), contentType = ContentType.Application.Json)
                    }

                    get("/board/{id}/statistics") {
                        val id = call.parameters["id"]
                        call.respondText(RequestExecuter.execute(GetBoardStatistics(id)), contentType = ContentType.Application.Json)
                    }

                    get("/processedBoard/{id}") {
                        val id = call.parameters["id"]
                        call.respondText(getBoardsAndProcessActionsOfAllCards(id), contentType = ContentType.Application.Json)
                    }

                    route("/testBoard") {
                        get {
                            call.respondText(getBoardsAndProcessActionsOfAllCards("RsU5w4Bn"), contentType = ContentType.Application.Json)
                        }
                    }

                    /**
                     * List
                     */

                    get("/list/{id}") {
                        val id = call.parameters["id"]
                        call.respondText(RequestExecuter.execute(GetList(id)), contentType = ContentType.Application.Json)
                    }

                    get("/list/{id}/detailed") {
                        val id = call.parameters["id"]
                        call.respondText(RequestExecuter.execute(GetDetailedList(id)), contentType = ContentType.Application.Json)
                    }

                    /**
                     * Card
                     */

                    get("/card/{id}") {
                        val id = call.parameters["id"]
                        call.respondText(RequestExecuter.execute(GetCard(id)), contentType = ContentType.Application.Json)
                    }

                    /**
                     * Action
                     */

                    get("/action/{id}") {
                        val id = call.parameters["id"]
                        call.respondText(RequestExecuter.execute(GetAction(id)), contentType = ContentType.Application.Json)
                    }

                    get("/card/{id}/actions") {
                        val id = call.parameters["id"]
                        call.respondText(RequestExecuter.execute(GetCardActions(id)), contentType = ContentType.Application.Json)
                    }

                }
            }
        }
    }
}

suspend fun getBoardsAndProcessActionsOfAllCards(id: String?): String {
    val gson = Gson()
    return try {
        val request = GetDetailedBoard(id)
        request.prepare()
        val board = request.execute()

        for (list in board.lists)
            for (card in list.cards) {
                val actionRequest = GetCardActions(card.id)
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
