package com.example.request.board

import com.example.helper.JsonHelper
import com.example.helper.Request
import com.example.helper.TrelloCall
import com.example.request.BaseTrelloRequest
import com.example.trello.model.Action

class GetLastBoardAction(val request: Request) : BaseTrelloRequest<Action>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "boards/${request.id}/actions"
        call.parameters["limit"] = "1"
        call.parameters["actions_fields"] = "type,date"
    }

    override suspend fun execute(): Action {
        val actions = JsonHelper.fromJson(gson, call, client, Array<Action>::class.java)
        client.close()
        return actions.first()
    }
}