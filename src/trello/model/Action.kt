package com.example.trello.model

import java.util.*

data class Action(
    val id: String,
    var data: Data,
    var date: Date,
    var type: String
)