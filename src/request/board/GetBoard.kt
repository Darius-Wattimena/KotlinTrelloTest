package com.example.request.board

import com.example.helper.TrelloCall
import com.example.helper.JsonHelper
import com.example.helper.Request
import com.example.request.BaseTrelloRequest
import com.example.trello.model.Board

class GetBoard(private val request: Request): BaseTrelloRequest<Board>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "boards/${request.id}"
    }

    override suspend fun execute(): Board {
        return JsonHelper.fromJson(gson, call, client, Board::class.java)
    }
}