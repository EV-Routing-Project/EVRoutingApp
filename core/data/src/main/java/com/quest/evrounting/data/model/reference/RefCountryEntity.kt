package com.quest.evrounting.data.model.reference

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RefCountry")
data class RefCountryEntity (
    @PrimaryKey
    @ColumnInfo(name = "ID")
    val id: Int,

    @ColumnInfo(name = "Title")
    val title: String,

    @ColumnInfo(name = "ISOCode")
    val isoCode: String
)