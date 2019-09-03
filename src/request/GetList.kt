package com.example.request

import com.example.TrelloCall
import com.example.helper.JsonHelper
import com.example.trello.model.List

class GetList(private val id: String?): BaseRequest<List>() {
    val call = TrelloCall()

    override fun prepare() {
        call.request = "lists/$id"
    }

    override suspend fun execute(): List {
        return JsonHelper.fromJson(gson, call, client, List::class.java)
    }
}