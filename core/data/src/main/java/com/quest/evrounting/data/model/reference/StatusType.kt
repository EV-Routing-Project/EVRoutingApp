package com.quest.evrounting.data.model.reference

import org.jetbrains.exposed.sql.Table

object StatusTypes : Table("RefStatusType") {
    val id = integer("ID")
    val title = varchar("Title", 255)
    val isOperational = bool("IsOperational").nullable()

    override val primaryKey = PrimaryKey(id)
}

data class StatusType(
    val id: Int,
    val title: String,
    val isOperational: Boolean?
)
