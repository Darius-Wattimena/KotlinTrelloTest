package com.example.request.member

import com.example.helper.JsonHelper
import com.example.helper.Request
import com.example.helper.TrelloCall
import com.example.request.BaseTrelloRequest
import com.example.trello.model.List
import com.example.trello.model.Member

class GetMember(private val request: Request) : BaseTrelloRequest<Member>() {
    private val call = TrelloCall(request.GetKey(), request.GetToken())

    override fun prepare() {
        call.request = "members/${request.id}"
    }

    override suspend fun execute(): Member {
        return JsonHelper.fromJson(gson, call, client, Member::class.java)
    }
}