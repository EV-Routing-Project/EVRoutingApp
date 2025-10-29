package com.quest.evrounting.data.model.dynamic

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Bảng thứ 11: Lưu trữ nhật ký (log) các thay đổi trạng thái của mỗi cổng sạc vật lý.
 * Bảng này cho phép theo dõi lịch sử hoạt động theo thời gian.
 */
@Entity(
    tableName = "PortStatusLog",
    foreignKeys = [
        ForeignKey(
            entity = LivePortStatusEntity::class,
            parentColumns = ["PortUID"],
            childColumns = ["PortUID"],
            // Khi một cổng sạc vật lý bị xóa, toàn bộ lịch sử của nó cũng bị xóa.
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    // Thêm index để tăng tốc truy vấn theo thời gian mô phỏng
    indices = [
        Index(value = ["PortUID"]),
        Index(value = ["Timestamp"]),
        Index(value = ["SimulationTimestamp"])]
)
data class PortStatusLogEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "LogID")
    val logId: Int = 0,

    @ColumnInfo(name = "PortUID")
    val portUID: String,

    /**
     * Thời điểm thực tế (real-world time) mà sự kiện thay đổi trạng thái xảy ra.
     * Dùng để hiển thị cho người dùng.
     */
    @ColumnInfo(name = "Timestamp")
    val timestamp: Date,

    /**
     * Thời gian mô phỏng, tính bằng đơn vị (giây/phút) từ lúc bắt đầu.
     * Dùng cho logic mô phỏng, testing và debug, tách biệt khỏi thời gian thực.
     */
    @ColumnInfo(name = "SimulationTimestamp")
    val simulationTimestamp: Long,

    /**
     * Trạng thái mới của cổng sau sự kiện (true = bận, false = trống).
     */
    @ColumnInfo(name = "NewStatus")
    val newStatus: Boolean,

    /**
     * Mô tả bằng chữ về sự kiện để dễ đọc.
     * Ví dụ: "Bắt đầu sạc", "Sạc hoàn tất", "Cổng tạm thời không khả dụng".
     */
    @ColumnInfo(name = "EventDescription")
    val eventDescription: String?
)