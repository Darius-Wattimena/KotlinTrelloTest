package com.example.request

import com.example.TrelloCall
import com.example.helper.JsonHelper
import com.example.trello.model.Board

class GetBoard(private val id: String?): BaseRequest<Board>() {
    val call = TrelloCall()

    override fun prepare() {
        call.request = "boards/$id"
    }

    override suspend fun execute(): Board {
        return JsonHelper.fromJson(gson, call, client, Board::class.java)
    }
}