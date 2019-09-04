package com.example

import com.example.helper.Request
import com.example.request.board.GetLastBoardAction
import com.example.request.GetCardActions
import com.example.request.GetTodayBurndownChartInfo
import com.example.request.action.GetAction
import com.example.request.board.GetBoard
import com.example.request.board.GetBoardStatistics
import com.example.request.board.GetDetailedBoard
import com.example.request.card.GetCard
import com.example.request.list.GetDetailedList
import com.example.request.list.GetList
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

                    get("/board/{id}/burndownchartinfo/") {
                        val request = Request(call.request.headers, call.parameters["id"]!!)
                        val doneListId = call.request.headers["doneListId"]
                        call.respondText(
                            RequestExecuter.execute(GetTodayBurndownChartInfo(request, doneListId.toString())),
                            contentType = ContentType.Application.Json
                        )
                    }

                    /**
                     * Board
                     */

                    get("/board/{id}") {
                        val request = Request(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetBoard(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                    get("/board/{id}/detailed") {
                        val request = Request(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetDetailedBoard(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                    get("/board/{id}/statistics") {
                        val request = Request(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetBoardStatistics(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                    get("/board/{id}/getLastAction") {
                        val request = Request(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetLastBoardAction(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                    get("/processedBoard/{id}") {
                        val request = Request(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            getBoardsAndProcessActionsOfAllCards(request),
                            contentType = ContentType.Application.Json
                        )
                    }

                    route("/testBoard") {
                        get {
                            val request = Request(call.request.headers, "RsU5w4Bn")
                            call.respondText(
                                getBoardsAndProcessActionsOfAllCards(request),
                                contentType = ContentType.Application.Json
                            )
                        }
                    }

                    /**
                     * List
                     */

                    get("/list/{id}") {
                        val request = Request(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetList(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                    get("/list/{id}/detailed") {
                        val request = Request(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetDetailedList(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                    /**
                     * Card
                     */

                    get("/card/{id}") {
                        val request = Request(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetCard(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                    /**
                     * Action
                     */

                    get("/action/{id}") {
                        val request = Request(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetAction(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                    get("/card/{id}/actions") {
                        val request = Request(call.request.headers, call.parameters["id"]!!)
                        call.respondText(
                            RequestExecuter.execute(GetCardActions(request)),
                            contentType = ContentType.Application.Json
                        )
                    }

                }
            }
        }
    }
}

suspend fun getBoardsAndProcessActionsOfAllCards(request: Request): String {
    val gson = Gson()
    return try {
        val trelloRequest = GetDetailedBoard(request)
        trelloRequest.prepare()
        val board = trelloRequest.execute()

        for (list in board.lists)
            for (card in list.cards) {
                val newRequest = request.copy(headers = request.headers, id = card.id)
                val actionRequest = GetCardActions(newRequest)
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
