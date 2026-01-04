package com.quest.evrouting.data.model.reference

import org.jetbrains.exposed.sql.Table

object Countries : Table("RefCountry") {
    val id = integer("ID")
    val title = varchar("Title", 255)
    val isoCode = varchar("ISOCode", 10)

    override val primaryKey = PrimaryKey(id)
}

data class Country(
    val id: Int,
    val title: String,
    val isoCode: String
)
