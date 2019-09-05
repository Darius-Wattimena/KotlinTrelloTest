package com.example.request.member

import com.example.helper.JsonHelper
import com.example.helper.Request
import com.example.helper.TrelloCall
import com.example.request.BaseTrelloRequest
import com.example.trello.model.Member

class GetBoardMembers(private val request: Request) : BaseTrelloRequest<Array<Member>>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "boards/${request.id}/members"
    }

    override suspend fun execute(): Array<Member> {
        return JsonHelper.fromJson(gson, call, client, Array<Member>::class.java)
    }
}