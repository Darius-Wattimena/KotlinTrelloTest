package com.example.trello.model

import java.time.LocalDate

data class BurndownChart(
    val items: MutableMap<LocalDate, BurndownChartItem>,
    val startDate: LocalDate,
    val endDate: LocalDate
)