package com.example.request

import com.example.helper.JsonHelper
import com.example.helper.Request
import com.example.helper.TrelloCall
import com.example.trello.model.Action

class GetCardActions(private val request: Request) : BaseTrelloRequest<Array<Action>>() {

    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "cards/${request.id}/actions"
    }

    override suspend fun execute(): Array<Action> {
        val actions = JsonHelper.fromJson(gson, call, client, Array<Action>::class.java)
        client.close()
        return actions
    }


}