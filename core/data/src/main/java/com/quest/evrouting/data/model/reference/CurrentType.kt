package com.quest.evrouting.data.model.reference

import org.jetbrains.exposed.sql.Table

object CurrentTypes : Table("RefCurrentType") {
    val id = integer("ID")
    val title = varchar("Title", 255)

    override val primaryKey = PrimaryKey(id)
}

data class CurrentType(
    val id: Int,
    val title: String
)
