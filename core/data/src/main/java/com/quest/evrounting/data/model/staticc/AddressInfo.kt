package com.quest.evrounting.data.model.staticc

import com.quest.evrounting.data.model.reference.Countries
import org.jetbrains.exposed.sql.*


object AddressInfos : Table("AddressInfo") {
    val id = integer("ID").autoIncrement()
    val title = varchar("Title", 255)
    val addressLine1 = varchar("AddressLine1", 500).nullable()
    val town = varchar("Town", 100).nullable()
    val postcode = varchar("Postcode", 20).nullable()

    val countryId = reference(
        "CountryID",
        Countries.id,
        onDelete = ReferenceOption.RESTRICT,
        onUpdate = ReferenceOption.CASCADE
    )

    val latitude = double("Latitude")
    val longitude = double("Longitude")
    val accessComments = text("AccessComments").nullable()

    val geohash12 = long("Geohash12")

    override val primaryKey = PrimaryKey(id)

    init {
        index(isUnique = false, countryId)
        index(isUnique = false, geohash12)
    }
}

data class AddressInfo(
    val id: Int,
    val title: String,
    val addressLine1: String?,
    val town: String?,
    val postcode: String?,
    val countryId: Int,
    val latitude: Double,
    val longitude: Double,
    val accessComments: String?,
    val geohash12: Long
)
