package com.example.request

import com.example.TrelloCall
import com.example.helper.JsonHelper
import com.example.trello.model.Board
import com.example.trello.model.Card
import com.example.trello.model.List

class GetDetailedBoard(private val id: String?) : BaseRequest<Board>() {
    private val boardCall = TrelloCall()
    private val boardListsCall = TrelloCall()

    override fun prepare() {
        boardCall.request = "boards/$id"
        boardListsCall.request = "boards/$id/lists"
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
            val listCardsCall = TrelloCall()
            listCardsCall.request = "lists/${list.id}/cards"
            val listCards = JsonHelper.fromJson(gson, listCardsCall, client, Array<Card>::class.java)
            list.cards = listCards
        }
        return boardLists
    }
}