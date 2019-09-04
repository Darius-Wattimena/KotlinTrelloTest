package com.example.request.board

import com.example.helper.TrelloCall
import com.example.helper.JsonHelper
import com.example.helper.Request
import com.example.request.BaseTrelloRequest
import com.example.trello.model.Board
import com.example.trello.model.Card
import com.example.trello.model.List

class GetDetailedBoard(private val request: Request) : BaseTrelloRequest<Board>() {
    private val boardCall = TrelloCall(request.GetKey(), request.GetToken())
    private val boardListsCall = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        boardCall.request = "boards/${request.id}"
        boardListsCall.request = "boards/${request.id}/lists"
    }

    override suspend fun execute(): Board {
        val board = JsonHelper.fromJson(gson, boardCall, client, Board::class.java)
        board.lists = getListAndCards(board)
        client.close()
        return board
    }

    private suspend fun getListAndCards(board: Board): Array<List> {
        val boardLists = JsonHelper.fromJson(gson, boardListsCall, client, Array<List>::class.java)

        for (list in boardLists) {
            val listCardsCall = TrelloCall(request.GetKey(), request.GetToken())
            listCardsCall.request = "lists/${list.id}/cards"
            val listCards = JsonHelper.fromJson(gson, listCardsCall, client, Array<Card>::class.java)
            list.cards = listCards
        }
        return boardLists
    }
}