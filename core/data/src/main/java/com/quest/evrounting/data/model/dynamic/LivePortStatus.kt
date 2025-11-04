package com.quest.evrounting.data.model.dynamic

import com.quest.evrounting.data.model.staticc.ChargePoints
import com.quest.evrounting.data.model.staticc.Connections
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.datetime // Sử dụng thư viện cho Date/Time

object LivePortStatuses : Table("LivePortStatus") {
    /**
     * Khóa chính, định danh duy nhất cho từng cổng sạc vật lý.
     * Ví dụ: "cp15_ct33_p1"
     * Đây là một cột VARCHAR, không tự tăng.
     */
    val portUID = varchar("PortUID", 255)

    val chargePointId = reference(
        "ChargePointID",
        ChargePoints.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )

    val connectionId = reference(
        "ConnectionID",
        Connections.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )

    /**
     * Chỉ lưu trạng thái BẬN (true) hoặc TRỐNG (false).
     * Không lưu các trạng thái cổng sạc "Không hoạt động.
     */
    val isOccupiedBySimulation = bool("IsOccupiedBySimulation").default(false)

    /**
     * Thời điểm cuối cùng kiểm tra trạng thái.
     */
    val lastChecked = datetime("LastChecked")

    override val primaryKey = PrimaryKey(portUID)

    init {
        index(isUnique = false, chargePointId)
        index(isUnique = false, connectionId)
    }
}

data class LivePortStatus(
    val portUID: String,
    val chargePointId: Int,
    val connectionId: Int,
    val isOccupiedBySimulation: Boolean,
    val lastChecked: java.time.LocalDateTime
)
