package com.quest.evrounting.data.model.dynamic

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.quest.evrounting.data.model.staticc.ChargePointEntity
import com.quest.evrounting.data.model.staticc.ConnectionEntity
import java.util.Date

/**
 * Bảng thứ 10: Lưu trữ trạng thái động của TỪNG CỔNG SẠC VẬT LÝ.
 * Chỉ lưu trữ trạng thái BẬN/TRỐNG do bộ giả lập sinh ra.
 * Bảng này KHÔNG lưu trạng thái "Không hoạt động".
 */
@Entity(
    tableName = "LivePortStatus",
    foreignKeys = [
        ForeignKey(
            entity = ChargePointEntity::class,
            parentColumns = ["ID"],
            childColumns = ["ChargePointID"],
            // Khi trạm sạc bị xóa, tất cả các cổng vật lý của nó cũng bị xóa.
            onDelete = ForeignKey.Companion.CASCADE
        ),
        ForeignKey(
            entity = ConnectionEntity::class,
            parentColumns = ["ID"],
            childColumns = ["ConnectionID"],
            // Khi một loại kết nối bị xóa, các cổng vật lý tương ứng cũng bị xóa.
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [
        Index(value = ["ChargePointID"]),
        Index(value = ["ConnectionID"])
    ]
)
data class LivePortStatusEntity(
    /**
     * Khóa chính, định danh duy nhất và dễ đọc cho từng cổng sạc vật lý.
     * Ví dụ: "cp15_ct33_p1"
     */
    @PrimaryKey
    @ColumnInfo(name = "PortUID")
    val portUID: String,

    /**
     * ID của trạm sạc chứa cổng này. Tham chiếu đến bảng ChargePoint.
     */
    @ColumnInfo(name = "ChargePointID")
    val chargePointId: Int,

    /**
     * ID của loại kết nối tương ứng. Tham chiếu đến bảng Connections.
     */
    @ColumnInfo(name = "ConnectionID")
    val connectionId: Int,

    /**
     * Chỉ lưu trạng thái BẬN (true) hoặc TRỐNG (false) do bộ giả lập sinh ra.
     * Không lưu các trạng thái cổng sạc "Không hoạt động".
     */
    @ColumnInfo(name = "IsOccupiedBySimulation", defaultValue = "0")
    val isOccupiedBySimulation: Boolean,

    @ColumnInfo(name = "LastChecked")
    val lastChecked: Date
)