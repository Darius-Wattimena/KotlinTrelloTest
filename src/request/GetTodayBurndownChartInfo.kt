package com.example.request

import com.example.helper.JsonHelper
import com.example.helper.Request
import com.example.helper.TrelloCall
import com.example.trello.model.BurndownChart
import com.example.trello.model.BurndownChartItem
import com.example.trello.model.Card
import com.example.trello.model.List
import java.time.LocalDate
import java.time.Period
import kotlin.collections.HashMap

class GetTodayBurndownChartInfo(val request: Request, val doneListId: String) : BaseTrelloRequest<BurndownChart>() {

    private val boardCall = TrelloCall(request.GetKey(), request.GetToken())
    private val bcDetails = BurndownChartDetails()

    override fun prepare() {
        boardCall.request = "/board/${request.id}/lists"
        boardCall.parameters["fields"] = "name"
    }

    override suspend fun execute(): BurndownChart {
        val lists = JsonHelper.fromJson(gson, boardCall, client, Array<List>::class.java)

        for (list in lists) {
            val listCall = TrelloCall(request.GetKey(), request.GetToken())
            listCall.request = "/lists/${list.id}/cards"

            val cards = JsonHelper.fromJson(gson, listCall, client, Array<Card>::class.java)

            val point = getAmount(cards, """\[[+-]?(\d*\.)?\d+\]""", "[", "]", 1)
            val hours = getAmount(cards, """\([+-]?(\d*\.)?\d+\)""", "(", ")", 2)
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

        val period = Period.ofDays(14)
        val startOfSprint = LocalDate.now()
        val endOfSprint = startOfSprint.plus(period)

        val burndownChart = BurndownChart(HashMap(), startOfSprint, endOfSprint)

        burndownChart.items[startOfSprint] = BurndownChartItem(startOfSprint,
            bcDetails.donePoint,
            bcDetails.doneItems,
            bcDetails.doneHoursSpend,
            bcDetails.point,
            bcDetails.items,
            bcDetails.hoursSpend,
            bcDetails.missingInfo)


        return burndownChart
    }

    private fun getAmount(cards: Array<Card>, regexPattern: String, prefix: String, suffix: String, errorIntensity: Int) : Float {
        var resultTotal = 0f
        for (card in cards) {
            val regex = Regex(regexPattern)
            val result = regex.find(card.name)
            if (result != null) {
                val resultString = result.value.removeSurrounding(prefix, suffix)
                resultTotal += resultString.toFloat()
            } else {
                if (bcDetails.missingInfo.containsKey(card.id)) {
                    bcDetails.missingInfo[card.id] = bcDetails.missingInfo[card.id]!! + errorIntensity
                }
                bcDetails.missingInfo[card.id] = errorIntensity
            }
        }

        return resultTotal
    }

    private class BurndownChartDetails {
        var donePoint = 0
        var doneItems = 0
        var doneHoursSpend = 0f
        var point = 0
        var items = 0
        var hoursSpend = 0f
        var missingInfo = HashMap<String, Int>()
    }
}