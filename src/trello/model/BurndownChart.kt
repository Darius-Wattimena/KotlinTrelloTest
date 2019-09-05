package com.example.trello.model

import trello.model.BurndownChartItem

data class BurndownChart(
    val items: MutableMap<Long, BurndownChartItem>,
    val startDate: Long,
    val endDate: Long
)