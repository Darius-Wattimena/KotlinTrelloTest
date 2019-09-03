package com.example.trello

data class Card (
    val id: String,
    var name: String,
    var labels: Array<Label>
)