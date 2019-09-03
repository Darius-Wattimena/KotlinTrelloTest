package com.example.model

data class Board (
    var id: String,
    var name: String,
    var desc: String,
    var url: String,
    var labels: Array<Label>
)