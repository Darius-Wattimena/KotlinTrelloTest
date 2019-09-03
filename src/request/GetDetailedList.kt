package com.example.request

import com.example.TrelloCall
import com.example.helper.JsonHelper
import com.example.trello.model.Card
import com.example.trello.model.List

class GetDetailedList(private val id: String?): BaseRequest<List>() {
    val call = TrelloCall()
    val cardsCall = TrelloCall()

    override fun prepare() {
        call.request = "lists/$id"
        cardsCall.request = "lists/$id/cards"
    }

    override suspend fun execute(): List {
        val list = JsonHelper.fromJson(gson, call, client, List::class.java)
        val cards = JsonHelper.fromJson(gson, cardsCall, client, Array<Card>::class.java)
        list.cards = cards
        return list
    }
}