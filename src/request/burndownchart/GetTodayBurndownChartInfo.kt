package com.example.request.burndownchart

import com.example.helper.Request
import com.example.helper.TrelloCall
import com.example.mongodb.DatabaseDriver
import com.example.request.BaseTrelloRequest
import trello.model.BurndownChartItem
import java.sql.Date
import java.time.LocalDate

class GetTodayBurndownChartInfo(
    val request: Request,
    private val doneListId: String,
    private val today: String
) : BaseTrelloRequest<BurndownChartItem>() {
    private val boardCall = TrelloCall(request.GetKey(), request.GetToken())
    private var bcDetails = BurndownChartDetails()
    private val driver = DatabaseDriver()

    override fun prepare() {
        boardCall.request = "/board/${request.id}/lists"
        boardCall.parameters["fields"] = "name"
    }

    override suspend fun execute(): BurndownChartItem {
        val today = LocalDate.parse(today)
        val todayDate = Date.valueOf(today).time

        val processor = DayProcessor(request, doneListId)
        bcDetails = processor.process(request, gson, boardCall, client)
        val todayItem = processor.convertToBurndownChartItem(bcDetails, todayDate)
        driver.save(todayItem)
        return todayItem
    }
}