package com.example.request.card

import com.example.helper.JsonHelper
import com.example.helper.Request
import com.example.helper.TrelloCall
import com.example.request.BaseTrelloRequest
import com.example.trello.model.Card

class GetCard(private val request: Request) : BaseTrelloRequest<Card>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "cards/${request.id}"
    }

    override suspend fun execute(): Card {
        return JsonHelper.fromJson(gson, call, client, Card::class.java)
    }
}