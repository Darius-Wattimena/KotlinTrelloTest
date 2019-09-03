package com.example.trello

data class List(
    val id: String,
    var name: String,
    var cards: Array<Card>
)