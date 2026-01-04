package com.quest.evrouting.data.model.reference

import org.jetbrains.exposed.sql.Table

object Operators : Table("RefOperator") {
    val id = integer("ID")
    val title = varchar("Title", 255)
    val websiteUrl = varchar("WebsiteUrl", 2048).nullable()

    override val primaryKey = PrimaryKey(id)
}

data class Operator(
    val id: Int,
    val title: String,
    val websiteUrl: String?
)
