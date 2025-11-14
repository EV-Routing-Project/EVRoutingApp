package com.quest.evrounting.data.model.staticc

import com.quest.evrounting.data.model.reference.Operators
import com.quest.evrounting.data.model.reference.UsageTypes
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.datetime // Sử dụng thư viện cho Date/Time

object ChargePoints : Table("ChargePoint") {
    // ID này từ OCM, không phải do app tự sinh, nên không dùng autoIncrement()
    val id = integer("ID")
    val uuid = varchar("UUID", 255).nullable()

    val addressInfoId = reference(
        "AddressInfoID",
        AddressInfos.id,
        onDelete = ReferenceOption.RESTRICT,
        onUpdate = ReferenceOption.CASCADE
    ).uniqueIndex()       // Chỉ định cột này là khóa ngoại duy nhất

    val operatorId = reference(
        "OperatorID",
        Operators.id,
        onDelete = ReferenceOption.RESTRICT,
        onUpdate = ReferenceOption.CASCADE
    ).nullable()

    val usageTypeId = reference(
        "UsageTypeID",
        UsageTypes.id,
        onDelete = ReferenceOption.RESTRICT,
        onUpdate = ReferenceOption.CASCADE
    ).nullable()

    override val primaryKey = PrimaryKey(id)

    // Bổ sung khối init để khai báo tường minh các chỉ mục cho khóa ngoại
    init {
        index(isUnique = false, addressInfoId)
        index(isUnique = false, operatorId)
        index(isUnique = false, usageTypeId)
    }
}

// Tạo data class tương ứng để chứa dữ liệu
data class ChargePoint(
    val id: Int,
    val uuid: String?,
    val addressInfoId: Int,
    val operatorId: Int?,
    val usageTypeId: Int?,
)
