package com.example.trello.model

import java.time.LocalDate

data class BurndownChartItem(
    val date: LocalDate,
    val totalDonePoint: Int,
    val totalDoneItems: Int,
    val totalDoneHoursSpend: Float,
    val totalPoint: Int,
    val totalItems: Int,
    val totalHoursSpend: Float,
    val faultyItems: HashMap<String, Int>
)
