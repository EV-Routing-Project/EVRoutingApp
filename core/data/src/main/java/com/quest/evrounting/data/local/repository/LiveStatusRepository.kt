package com.quest.evrounting.data.local.repository

import com.quest.evrounting.data.model.dynamic.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime

/**
 * Repository quản lý các bảng dữ liệu động: Trạng thái trực tiếp (LivePortStatus) và Lịch sử trạng thái (PortStatusLog).
 * Phục vụ cho bộ giả lập trạng thái sạc.
 */
object LiveStatusRepository {

    //region --- HÀM CHUYỂN ĐỔI (CONVERTERS) ---

    private fun toLivePortStatus(row: ResultRow): LivePortStatus = LivePortStatus(
        portUID = row[LivePortStatuses.portUID],
        chargePointId = row[LivePortStatuses.chargePointId],
        connectionId = row[LivePortStatuses.connectionId],
        isOccupiedBySimulation = row[LivePortStatuses.isOccupiedBySimulation],
        lastChecked = row[LivePortStatuses.lastChecked]
    )

    private fun toPortStatusLog(row: ResultRow): PortStatusLog = PortStatusLog(
        logId = row[PortStatusLogs.logId],
        portUID = row[PortStatusLogs.portUID],
        timestamp = row[PortStatusLogs.timestamp],
        simulationTimestamp = row[PortStatusLogs.simulationTimestamp],
        newStatus = row[PortStatusLogs.newStatus],
        eventDescription = row[PortStatusLogs.eventDescription]
    )
    //endregion

    //region --- THAO TÁC VỚI LIVEPORTSTATUS ---

    /**
     * Chèn hoặc cập nhật một danh sách các cổng sạc vật lý. Dùng cho việc khởi tạo dữ liệu.
     */
    suspend fun upsertLivePortStatuses(ports: List<LivePortStatus>) {
        newSuspendedTransaction<Unit> {
            LivePortStatuses.batchUpsert(ports) { port ->
                this[LivePortStatuses.portUID] = port.portUID
                this[LivePortStatuses.chargePointId] = port.chargePointId
                this[LivePortStatuses.connectionId] = port.connectionId
                this[LivePortStatuses.isOccupiedBySimulation] = port.isOccupiedBySimulation
                this[LivePortStatuses.lastChecked] = port.lastChecked
            }
        }
    }

    /**
     * Cập nhật trạng thái của một cổng sạc duy nhất.
     */
    suspend fun updateLivePortStatus(portUID: String, isOccupiedBySimulation: Boolean, lastChecked: LocalDateTime) {
        newSuspendedTransaction<Unit> {
            LivePortStatuses.update({ LivePortStatuses.portUID eq portUID }) {
                it[this.isOccupiedBySimulation] = isOccupiedBySimulation
                it[this.lastChecked] = lastChecked
            }
        }
    }

    /**
     * Lấy trạng thái của tất cả các cổng vật lý thuộc về một trạm sạc.
     */
    fun getLiveStatusForChargePoint(chargePointId: Int): Flow<List<LivePortStatus>> = flow {
        val items = newSuspendedTransaction {
            LivePortStatuses.selectAll()
                .where { LivePortStatuses.chargePointId eq chargePointId }
                .map(::toLivePortStatus)
        }
        emit(items)
    }

    //endregion

    //region --- THAO TÁC VỚI PORTSTATUSLOG ---

    /**
     * Ghi một sự kiện mới vào bảng log lịch sử.
     */
    suspend fun insertPortStatusLog(logEntry: PortStatusLog) {
        newSuspendedTransaction<Unit> {
            PortStatusLogs.insert {
                it[logId] = logEntry.logId
                it[portUID] = logEntry.portUID
                it[timestamp] = logEntry.timestamp
                it[simulationTimestamp] = logEntry.simulationTimestamp
                it[newStatus] = logEntry.newStatus
                it[eventDescription] = logEntry.eventDescription
            }
        }
    }

    /**
     * Lấy toàn bộ lịch sử của một cổng sạc cụ thể, sắp xếp theo thời gian mô phỏng.
     */
    fun getLogsForPort(portUID: String): Flow<List<PortStatusLog>> = flow {
        val items = newSuspendedTransaction {
            PortStatusLogs.selectAll()
                .where { PortStatusLogs.portUID eq portUID }
                .orderBy(PortStatusLogs.simulationTimestamp to SortOrder.ASC)
                .map(::toPortStatusLog)
        }
        emit(items)
    }

    /**
     * Xóa các log cũ hơn một mốc thời gian mô phỏng nào đó.
     */
    suspend fun deleteOldLogs(simulationTimestamp: Long) {
        newSuspendedTransaction<Unit> {
            PortStatusLogs.deleteWhere { PortStatusLogs.simulationTimestamp less simulationTimestamp }
        }
    }

    //endregion
}
