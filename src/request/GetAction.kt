package com.example.request

import com.example.TrelloCall
import com.example.helper.JsonHelper
import com.example.trello.model.Action

class GetAction(private val id: String?): BaseRequest<Action>() {
    val call = TrelloCall()

    override fun prepare() {
        call.request = "actions/$id"
    }

    override suspend fun execute(): Action {
        return JsonHelper.fromJson(gson, call, client, Action::class.java)
    }
}