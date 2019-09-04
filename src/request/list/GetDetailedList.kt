package com.example.request.list

import com.example.helper.TrelloCall
import com.example.helper.JsonHelper
import com.example.helper.Request
import com.example.request.BaseTrelloRequest
import com.example.trello.model.Card
import com.example.trello.model.List

class GetDetailedList(private val request: Request): BaseTrelloRequest<List>() {
    val call = TrelloCall(request.GetKey(), request.GetToken())
    val cardsCall = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "lists/${request.id}"
        cardsCall.request = "lists/${request.id}/cards"
    }

    override suspend fun execute(): List {
        val list = JsonHelper.fromJson(gson, call, client, List::class.java)
        val cards = JsonHelper.fromJson(gson, cardsCall, client, Array<Card>::class.java)
        list.cards = cards
        return list
    }
}