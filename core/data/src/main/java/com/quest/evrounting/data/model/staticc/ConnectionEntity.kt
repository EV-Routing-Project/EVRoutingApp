package com.quest.evrounting.data.model.staticc

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.quest.evrounting.data.model.reference.RefConnectionTypeEntity
import com.quest.evrounting.data.model.reference.RefCurrentTypeEntity
import com.quest.evrounting.data.model.reference.RefStatusTypeEntity

@Entity(
    tableName = "Connections",
    foreignKeys = [
        ForeignKey(
            entity = ChargePointEntity::class,
            parentColumns = ["ID"], childColumns = ["ChargePointID"],
            // Khi ChargePoint bị xóa, các Connection liên quan cũng bị xóa theo.
            onDelete = ForeignKey.Companion.CASCADE,
            onUpdate = ForeignKey.Companion.CASCADE
        ),
        ForeignKey(
            entity = RefConnectionTypeEntity::class,
            parentColumns = ["ID"], childColumns = ["ConnectionTypeID"],
            onDelete = ForeignKey.Companion.RESTRICT, onUpdate = ForeignKey.Companion.CASCADE
        ),
        ForeignKey(
            entity = RefCurrentTypeEntity::class,
            parentColumns = ["ID"], childColumns = ["CurrentTypeID"],
            onDelete = ForeignKey.Companion.RESTRICT, onUpdate = ForeignKey.Companion.CASCADE
        ),
        ForeignKey(
            entity = RefStatusTypeEntity::class,
            parentColumns = ["ID"], childColumns = ["StatusTypeID"],
            onDelete = ForeignKey.Companion.RESTRICT, onUpdate = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [
        Index(value = ["ChargePointID"]),
        Index(value = ["ConnectionTypeID"]),
        Index(value = ["CurrentTypeID"]),
        Index(value = ["StatusTypeID"])
    ]
)
data class ConnectionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    val id: Int = 0,

    @ColumnInfo(name = "ChargePointID")
    val chargePointId: Int,

    @ColumnInfo(name = "ConnectionTypeID")
    val connectionTypeId: Int,

    @ColumnInfo(name = "CurrentTypeID")
    val currentTypeId: Int?,

    @ColumnInfo(name = "StatusTypeID")
    val statusTypeId: Int?,

    @ColumnInfo(name = "PowerKW")
    val powerKw: Double?,

    @ColumnInfo(name = "Quantity")
    val quantity: Int?
)