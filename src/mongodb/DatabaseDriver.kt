package com.example.mongodb

import org.litote.kmongo.*
import trello.model.BurndownChartItem

class DatabaseDriver {
    val client = KMongo.createClient()
    private val database = client.getDatabase("test")
    private val collection = database.getCollection<BurndownChartItem>()

    fun saveBurndownChartItem(item: BurndownChartItem) {
        val dbItem = collection.findOne(BurndownChartItem::date eq item.date)
        if (dbItem == null) {
            insertBurndownChartItem(item)
        } else {
            updateBurndownChartItem(item, dbItem)
        }
    }

    fun insertBurndownChartItem(item: BurndownChartItem) {
        collection.insertOne(item)
    }

    fun updateBurndownChartItem(item: BurndownChartItem, dbItem: BurndownChartItem) {
        collection.updateOne(BurndownChartItem::date eq dbItem.date, item)
    }

    fun findBurndownChartItem(epochDate: Long) : BurndownChartItem? {
        return collection.findOne(BurndownChartItem::date eq epochDate)
    }
}