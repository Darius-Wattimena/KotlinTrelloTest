package com.example.request.board

import com.example.helper.JsonHelper
import com.example.helper.Request
import com.example.helper.TrelloCall
import com.example.request.BaseTrelloRequest
import com.example.trello.model.Board
import com.example.trello.model.ListStatistics
import com.example.trello.model.Statistics

class GetBoardStatistics(private val request: Request) : BaseTrelloRequest<Statistics>() {
    private val boardCall = TrelloCall(request.GetKey(), request.GetToken())
    private val listsCall = TrelloCall(request.GetKey(), request.GetToken())

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
            list.labelAmounts = HashMap()
            for (card in list.cards!!) {
                for (label in card.labels) {
                    if (!list.labelAmounts.containsKey(label.name)) {
                        list.labelAmounts[label.name] = 1
                    } else {
                        val newValue = list.labelAmounts[label.name]!! + 1
                        list.labelAmounts[label.name] = newValue
                    }
                }
            }

            list.cards = null
        }

        return Statistics(board.id, board.name, lists)
    }
}