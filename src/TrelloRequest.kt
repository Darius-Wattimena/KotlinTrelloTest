package com.example

interface TrelloRequest<T> {
    fun prepare()
    suspend fun execute(): T
}