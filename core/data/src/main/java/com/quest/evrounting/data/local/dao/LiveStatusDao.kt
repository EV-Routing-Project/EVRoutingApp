package com.quest.evrounting.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.quest.evrounting.data.model.dynamic.LivePortStatusEntity
import com.quest.evrounting.data.model.dynamic.PortStatusLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object cho các bảng dữ liệu động: LivePortStatus và PortStatusLog.
 * Phục vụ cho bộ giả lập trạng thái sạc.
 */
@Dao
interface LiveStatusDao {

    // --- LivePortStatus Queries ---

    /**
     * Chèn một danh sách các cổng sạc vật lý. Dùng cho việc khởi tạo dữ liệu.
     * Nếu một cổng đã tồn tại, nó sẽ được thay thế.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLivePortStatuses(ports: List<LivePortStatusEntity>)

    /**
     * Cập nhật trạng thái của một cổng sạc duy nhất.
     * Hàm này sẽ được bộ giả lập gọi liên tục.
     */
    @Update
    suspend fun updateLivePortStatus(port: LivePortStatusEntity)

    /**
     * Lấy trạng thái của tất cả các cổng vật lý thuộc về một trạm sạc.
     * @param chargePointId ID của trạm sạc.
     * @return Một Flow chứa danh sách trạng thái các cổng của trạm đó.
     */
    @Query("SELECT * FROM LivePortStatus WHERE ChargePointID = :chargePointId")
    fun getLiveStatusForChargePoint(chargePointId: Int): Flow<List<LivePortStatusEntity>>

    // --- PortStatusLog Queries ---

    /**
     * Ghi một sự kiện mới vào bảng log lịch sử.
     */
    @Insert
    suspend fun insertPortStatusLog(logEntry: PortStatusLogEntity)

    /**
     * Lấy toàn bộ lịch sử của một cổng sạc cụ thể, sắp xếp theo thời gian mô phỏng.
     * @param portUID ID duy nhất của cổng sạc.
     * @return Một Flow chứa danh sách lịch sử của cổng đó.
     */
    @Query("SELECT * FROM PortStatusLog WHERE PortUID = :portUID ORDER BY SimulationTimestamp ASC")
    fun getLogsForPort(portUID: String): Flow<List<PortStatusLogEntity>>

    /**
     * Xóa các log cũ hơn một mốc thời gian mô phỏng nào đó.
     * Dùng để dọn dẹp database, tránh bị phình to.
     * @param simulationTimestamp Mốc thời gian mô phỏng để xóa các log cũ hơn.
     */
    @Query("DELETE FROM PortStatusLog WHERE SimulationTimestamp < :simulationTimestamp")
    suspend fun deleteOldLogs(simulationTimestamp: Long)
}
