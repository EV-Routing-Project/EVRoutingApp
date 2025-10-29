package com.quest.evrounting.data.model.staticc

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.quest.evrounting.data.model.reference.RefOperatorEntity
import com.quest.evrounting.data.model.reference.RefStatusTypeEntity
import com.quest.evrounting.data.model.reference.RefUsageTypeEntity
import java.util.Date

@Entity(tableName = "ChargePoint",
    foreignKeys = [
        ForeignKey(
            entity = AddressInfoEntity::class,
            parentColumns = ["ID"], childColumns = ["AddressInfoID"],
            onDelete = ForeignKey.Companion.RESTRICT, onUpdate = ForeignKey.Companion.CASCADE
        ),
        ForeignKey(
            entity = RefOperatorEntity::class,
            parentColumns = ["ID"], childColumns = ["OperatorID"],
            onDelete = ForeignKey.Companion.RESTRICT, onUpdate = ForeignKey.Companion.CASCADE
        ),
        ForeignKey(
            entity = RefUsageTypeEntity::class,
            parentColumns = ["ID"], childColumns = ["UsageTypeID"],
            onDelete = ForeignKey.Companion.RESTRICT, onUpdate = ForeignKey.Companion.CASCADE
        ),
        ForeignKey(
            entity = RefStatusTypeEntity::class,
            parentColumns = ["ID"], childColumns = ["StatusTypeID"],
            onDelete = ForeignKey.Companion.RESTRICT, onUpdate = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [
        Index(value = ["AddressInfoID"]),
        Index(value = ["OperatorID"]),
        Index(value = ["UsageTypeID"]),
        Index(value = ["StatusTypeID"])
    ]
)
data class ChargePointEntity(
    // ID này từ OCM, không phải do app tự sinh, nên không có autoGenerate = true
    @PrimaryKey
    @ColumnInfo(name = "ID")
    val id: Int,

    @ColumnInfo(name = "UUID")
    val uuid: String?,

    @ColumnInfo(name = "AddressInfoID")
    val addressInfoId: Int,

    @ColumnInfo(name = "OperatorID")
    val operatorId: Int?,

    @ColumnInfo(name = "UsageTypeID")
    val usageTypeId: Int?,

    @ColumnInfo(name = "NumberOfPoints")
    val numberOfPoints: Int?,

    @ColumnInfo(name = "StatusTypeID")
    val statusTypeId: Int?,

    // Room có thể lưu trữ kiểu Date, nó sẽ tự chuyển thành kiểu Timestamp (Long)
    @ColumnInfo(name = "DateLastStatusUpdate")
    val dateLastStatusUpdate: Date?
)