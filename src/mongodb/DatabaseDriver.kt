package com.example.mongodb

import org.litote.kmongo.*
import trello.model.BurndownChartItem

class DatabaseDriver() {

    val client = KMongo.createClient()
    private val database = client.getDatabase("test")
    private val collection = database.getCollection<BurndownChartItem>()

    fun save(item: BurndownChartItem) {
        val dbItem = collection.findOne(BurndownChartItem::date eq item.date)
        if (dbItem == null) {
            insert(item)
        } else {
            update(item, dbItem)
        }
    }

    fun insert(item: BurndownChartItem) {
        collection.insertOne(item)
    }

    fun update(item: BurndownChartItem, dbItem: BurndownChartItem) {
        collection.updateOne(BurndownChartItem::date eq dbItem.date, item)
    }

    fun find(epochDate: Long) : BurndownChartItem? {
        return collection.findOne(BurndownChartItem::date eq epochDate)
    }
}