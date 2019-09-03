package com.example.request

import com.example.TrelloCall
import com.example.helper.JsonHelper
import com.example.trello.model.Action

class GetActions(private val cardId: String?): BaseRequest<Array<Action>>() {

    private val actionsCall = TrelloCall()

    override fun prepare() {
        actionsCall.request = "cards/$cardId/actions"
    }

    override suspend fun execute(): Array<Action> {
        val actions = JsonHelper.fromJson(gson, actionsCall, client, Array<Action>::class.java)
        client.close()
        return actions
    }


}