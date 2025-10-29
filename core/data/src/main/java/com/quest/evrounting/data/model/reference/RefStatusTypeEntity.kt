package com.quest.evrounting.data.model.reference

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RefStatusType")
data class RefStatusTypeEntity(
    @PrimaryKey
    @ColumnInfo(name = "ID")
    val id: Int,

    @ColumnInfo(name = "Title")
    val title: String,

    // TINYINT(1) trong SQL thường được dùng cho giá trị boolean.
    // Room sẽ tự động chuyển đổi giữa Boolean (trong Kotlin) và Integer 0/1 (trong SQLite).
    @ColumnInfo(name = "IsOperational")
    val isOperational: Boolean?
)