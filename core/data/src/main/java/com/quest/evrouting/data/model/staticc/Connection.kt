package com.quest.evrouting.data.model.staticc

import com.quest.evrouting.data.model.reference.ConnectionTypes
import com.quest.evrouting.data.model.reference.CurrentTypes
import org.jetbrains.exposed.sql.*

object Connections : Table("Connections") {
    val id = integer("ID")

    val chargePointId = reference(
        "ChargePointID",
        ChargePoints.id,
        onDelete = ReferenceOption.CASCADE,  // Khi xóa ChargePoint, Connections bị xóa theo
        onUpdate = ReferenceOption.CASCADE
    )

    val connectionTypeId = reference(
        "ConnectionTypeID",
        ConnectionTypes.id,
        onDelete = ReferenceOption.RESTRICT,
        onUpdate = ReferenceOption.CASCADE
    )

    val currentTypeId = reference(
        "CurrentTypeID",
        CurrentTypes.id,
        onDelete = ReferenceOption.RESTRICT,
        onUpdate = ReferenceOption.CASCADE
    ).nullable()

    val powerKw = double("PowerKW").nullable()
    val quantity = integer("Quantity").nullable()

    override val primaryKey = PrimaryKey(id)

    // Khai báo tường minh các chỉ mục cho khóa ngoại để code rõ ràng
    init {
        index(isUnique = false, chargePointId)
        index(isUnique = false, connectionTypeId)
        index(isUnique = false, currentTypeId)
    }
}

//  Tạo data class tương ứng để chứa dữ liệu
data class Connection(
    val id: Int,
    val chargePointId: Int,
    val connectionTypeId: Int,
    val currentTypeId: Int?,
    val powerKw: Double?,
    val quantity: Int?
)

