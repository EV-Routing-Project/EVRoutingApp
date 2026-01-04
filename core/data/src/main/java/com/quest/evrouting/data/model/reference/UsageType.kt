package com.quest.evrouting.data.model.reference

import org.jetbrains.exposed.sql.Table

object UsageTypes : Table("RefUsageType") {
    val id = integer("ID")
    val title = varchar("Title", 255)

    override val primaryKey = PrimaryKey(id)
}

data class UsageType(
    val id: Int,
    val title: String
)
