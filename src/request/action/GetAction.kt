package com.example.request.action

import com.example.helper.TrelloCall
import com.example.helper.JsonHelper
import com.example.helper.Request
import com.example.request.BaseTrelloRequest
import com.example.trello.model.Action

class GetAction(private val request: Request): BaseTrelloRequest<Action>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "actions/${request.id}"
    }

    override suspend fun execute(): Action {
        return JsonHelper.fromJson(gson, call, client, Action::class.java)
    }
}