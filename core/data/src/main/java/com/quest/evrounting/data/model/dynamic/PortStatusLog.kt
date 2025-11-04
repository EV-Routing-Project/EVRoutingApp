package com.quest.evrounting.data.model.dynamic

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.datetime

/**
 * Bảng này lưu trữ nhật ký (log) các thay đổi trạng thái của mỗi cổng sạc vật lý.
 */
object PortStatusLogs : Table("PortStatusLog") {
    val logId = integer("LogID").autoIncrement()

    val portUID = reference(
        "PortUID",
        LivePortStatuses.portUID,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )

    /**
     * Thời điểm thực tế (real-world time) mà sự kiện thay đổi trạng thái xảy ra.
     */
    val timestamp = datetime("Timestamp")

    /**
     * Thời gian mô phỏng, tính bằng đơn vị (giây/phút) từ lúc bắt đầu.
     */
    val simulationTimestamp = long("SimulationTimestamp")

    /**
     * Trạng thái mới của cổng sau sự kiện (true = bận, false = trống).
     */
    val newStatus = bool("NewStatus")

    /**
     * Mô tả bằng chữ về sự kiện để dễ đọc.
     */
    val eventDescription = varchar("EventDescription", 255).nullable()

    init {
        index(isUnique = false, portUID)
        index(isUnique = false, timestamp)
        index(isUnique = false, simulationTimestamp)
    }
}

data class PortStatusLog(
    val logId: Int,
    val portUID: String,
    val timestamp: java.time.LocalDateTime,
    val simulationTimestamp: Long,
    val newStatus: Boolean,
    val eventDescription: String?
)

