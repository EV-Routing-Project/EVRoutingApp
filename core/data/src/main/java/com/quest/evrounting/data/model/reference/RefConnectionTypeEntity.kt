package com.quest.evrounting.data.model.reference

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RefConnectionType")
data class RefConnectionTypeEntity(
    @PrimaryKey
    @ColumnInfo(name = "ID")
    val id: Int,

    @ColumnInfo(name = "Title")
    val title: String,

    // Cột này có thể là null trong SQL (`VARCHAR NULL`)
    @ColumnInfo(name = "FormalName")
    val formalName: String?
)