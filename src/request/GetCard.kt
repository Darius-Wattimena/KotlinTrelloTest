package com.example.request

import com.example.TrelloCall
import com.example.helper.JsonHelper
import com.example.trello.model.Card

class GetCard(private val id: String?): BaseRequest<Card>() {
    val call = TrelloCall()

    override fun prepare() {
        call.request = "cards/$id"
    }

    override suspend fun execute(): Card {
        return JsonHelper.fromJson(gson, call, client, Card::class.java)
    }
}