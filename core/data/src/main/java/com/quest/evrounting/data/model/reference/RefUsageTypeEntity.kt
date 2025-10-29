package com.quest.evrounting.data.model.reference

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RefUsageType")
data class RefUsageTypeEntity(
    @PrimaryKey
    @ColumnInfo(name = "ID")
    val id: Int,

    @ColumnInfo(name = "Title")
    val title: String
)