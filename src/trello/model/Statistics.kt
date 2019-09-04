package com.example.trello.model

data class Statistics(
    val boardId: String,
    val boardName: String,
    val listsStatistics: Array<ListStatistics>
)