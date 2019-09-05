package com.example.request

import com.example.helper.JsonHelper
import com.example.helper.Request
import com.example.helper.TrelloCall
import com.example.trello.model.BurndownChart
import com.example.trello.model.Card
import com.example.trello.model.List
import trello.model.BurndownChartItem
import java.sql.Date
import java.time.LocalDate

class GetTodayBurndownChartInfo(
    val request: Request,
    private val doneListId: String,
    private val startDate: String,
    private val endDate: String,
    private val today: String
) : BaseTrelloRequest<BurndownChart>() {

    private val boardCall = TrelloCall(request.GetKey(), request.GetToken())
    private val bcDetails = BurndownChartDetails()

    override fun prepare() {
        boardCall.request = "/board/${request.id}/lists"
        boardCall.parameters["fields"] = "name"
    }

    override suspend fun execute(): BurndownChart {
        val startOfSprint = LocalDate.parse(startDate)
        val endOfSprint = LocalDate.parse(endDate)
        val startDate = Date.valueOf(startOfSprint).time
        val endDate = Date.valueOf(endOfSprint).time

        val burndownChart = BurndownChart(HashMap(), startDate, endDate)

        if (today != "null") {
            val lists = JsonHelper.fromJson(gson, boardCall, client, Array<List>::class.java)

            for (list in lists) {
                val listCall = TrelloCall(request.GetKey(), request.GetToken())
                listCall.request = "/lists/${list.id}/cards"

                val cards = JsonHelper.fromJson(gson, listCall, client, Array<Card>::class.java)

                val point = getAmount(list, cards, """\[[+-]?(\d*\.)?\d+\]""", "[", "]")
                val hours = getAmount(list, cards, """\([+-]?(\d*\.)?\d+\)""", "(", ")")
                //val hours = 1

                if (list.id == doneListId) {
                    bcDetails.donePoint += point.toInt()
                    bcDetails.doneItems += cards.size
                    bcDetails.doneHoursSpend += hours
                }

                bcDetails.point += point.toInt()
                bcDetails.items += cards.size
                bcDetails.hoursSpend += hours
            }

            burndownChart.items[startDate] = BurndownChartItem(
                bcDetails.donePoint,
                bcDetails.doneItems,
                bcDetails.doneHoursSpend,
                bcDetails.point,
                bcDetails.items,
                bcDetails.hoursSpend,
                bcDetails.missingInfo
            )

            //TODO save made item in the database
        }

        //TODO get all other known days from the database

        return burndownChart
    }

    private fun getAmount(list: List, cards: Array<Card>, regexPattern: String, prefix: String, suffix: String) : Float {
        var resultTotal = 0f
        for (card in cards) {
            val regex = Regex(regexPattern)
            val result = regex.find(card.name)
            if (result != null) {
                val resultString = result.value.removeSurrounding(prefix, suffix)
                val resultValue = resultString.toFloat()
                resultTotal += resultValue
                if (resultValue == 0f && list.id == doneListId) {
                    processZeroHoursOnDoneItem(card, resultString)
                }

            } else {
                processMissingInfo(card)
            }
        }

        return resultTotal
    }

    private fun processMissingInfo(card: Card) {
        bcDetails.missingInfo[card.id] = true
    }

    private fun processZeroHoursOnDoneItem(card: Card, result: String) {
        if (result.toFloat() == 0f) {
            processMissingInfo(card)
        }
    }

    private class BurndownChartDetails {
        var donePoint = 0
        var doneItems = 0
        var doneHoursSpend = 0f
        var point = 0
        var items = 0
        var hoursSpend = 0f
        var missingInfo = HashMap<String, Boolean>()
    }
}