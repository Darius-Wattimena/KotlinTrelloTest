package com.example.request.board

import com.example.TrelloCall
import com.example.helper.JsonHelper
import com.example.helper.Request
import com.example.request.BaseTrelloRequest
import com.example.trello.model.Board
import com.example.trello.model.ListStatistics
import com.example.trello.model.Statistics

class GetBoardStatistics(private val request: Request): BaseTrelloRequest<Statistics>() {
    val boardCall = TrelloCall(request.GetKey(), request.GetToken())
    val listsCall = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        boardCall.request = "board/${request.id}"
        boardCall.parameters["fields"] = "name"

        listsCall.request = "board/${request.id}/lists"
        listsCall.parameters["fields"] = "name"
        listsCall.parameters["cards"] = "all"
        listsCall.parameters["card_fields"] = "labels, name"
    }

    override suspend fun execute(): Statistics {
        val board = JsonHelper.fromJson(gson, boardCall, client, Board::class.java)

        val lists = JsonHelper.fromJson(gson, listsCall, client, Array<ListStatistics>::class.java)

        for (list in lists) {
            var totalFrontend = 0
            var totalBackend = 0
            for (card in list.cards) {
                for (label in card.labels) {
                    if (label.name == "FRONTEND")
                        totalFrontend++
                    else if (label.name == "BACKEND")
                        totalBackend++
                }
            }
            list.frontendAmount = totalFrontend
            list.backendAmount = totalBackend
        }

        return Statistics(board.id, board.name, lists)
    }
}