package com.quest.evrounting.data.model.reference

import org.jetbrains.exposed.sql.Table

  // từ khóa object trong kotlin là 1 singleton (chỉ tồn tại 1 đối tượng duy nhất trong toàn bộ ứng dụng)
object ConnectionTypes : Table("RefConnectionType") {
    val id = integer("ID")
    val title = varchar("Title", 255)
    val formalName = varchar("FormalName", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}

// Tạo data class tương ứng để làm đối tượng truyền dữ liệu (DTO)
data class ConnectionType(
    val id: Int,
    val title: String,
    val formalName: String?
)
