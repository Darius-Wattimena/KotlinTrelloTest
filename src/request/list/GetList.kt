package com.example.request.list

import com.example.helper.JsonHelper
import com.example.helper.Request
import com.example.helper.TrelloCall
import com.example.request.BaseTrelloRequest
import com.example.trello.model.List

class GetList(private val request: Request) : BaseTrelloRequest<List>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "lists/${request.id}"
    }

    override suspend fun execute(): List {
        return JsonHelper.fromJson(gson, call, client, List::class.java)
    }
}