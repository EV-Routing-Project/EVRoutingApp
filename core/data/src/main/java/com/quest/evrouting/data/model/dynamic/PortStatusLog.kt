package com.quest.evrouting.data.model.dynamic

import com.quest.evrouting.data.model.staticc.Connections
import org.jetbrains.exposed.sql.*

/**
 * Bảng này lưu trữ nhật ký (log) các thay đổi trạng thái của mỗi cổng sạc vật lý.
 */
object PortStatusLogs : Table("PortStatusLog") {
    val logId = integer("LogID").autoIncrement()

    val connectionId = reference(
        "ConnectionID",
        Connections.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )

    // Số lượng cổng sạc còn trống
    val availablePorts = integer("AvailablePorts")

    /**
     * Thời gian mô phỏng, tính bằng đơn vị (giây/phút) từ lúc bắt đầu.
     */
    val simulationTimestamp = long("SimulationTimestamp")

    override val primaryKey = PrimaryKey(logId)

    init {
        index(isUnique = false, connectionId, simulationTimestamp)
    }
}

data class PortStatusLog(
    val logId: Int = 0,
    val connectionId: Int,
    var availablePorts: Int,
    var simulationTimestamp: Long,
)

