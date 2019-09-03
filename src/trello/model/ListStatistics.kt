package com.example.trello.model

data class ListStatistics (
    val name: String,
    val cards: Array<Card>,
    var frontendAmount: Int,
    var backendAmount: Int
)