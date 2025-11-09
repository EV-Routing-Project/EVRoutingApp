package com.quest.evrounting.data.local.repository

import com.quest.evrounting.data.model.dynamic.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


object PortStatusRepository {

    private fun toPortStatusLog(row: ResultRow): PortStatusLog = PortStatusLog(
        logId = row[PortStatusLogs.logId],
        connectionId = row[PortStatusLogs.connectionId],
        availablePorts = row[PortStatusLogs.availablePorts],
        simulationTimestamp = row[PortStatusLogs.simulationTimestamp]
    )

    /**
     * Ghi một sự kiện thay đổi số lượng cổng trống vào log.
     */
    suspend fun insertPortStatusLog(logEntry: PortStatusLog) {
        newSuspendedTransaction<Unit> {
            PortStatusLogs.insert {
                // logId sẽ được tự động tạo bởi cơ sở dữ liệu nên không cần điền logId
                it[connectionId] = logEntry.connectionId
                it[availablePorts] = logEntry.availablePorts
                it[simulationTimestamp] = logEntry.simulationTimestamp
            }
        }
    }

    /**
     * Lấy trạng thái MỚI NHẤT (live status) của một loại cổng sạc (Connection).
     * @param connectionId ID của bản ghi trong bảng Connections.
     * @return Một Flow chứa bản ghi log cuối cùng, hoặc null nếu chưa có.
     */
    fun getLiveStatusForConnection(connectionId: Int): Flow<PortStatusLog?> = flow {
        val latestLog = newSuspendedTransaction {
            PortStatusLogs.selectAll().where { PortStatusLogs.connectionId eq connectionId }
                .orderBy(PortStatusLogs.simulationTimestamp, SortOrder.DESC)
                .limit(1)
                .map(::toPortStatusLog)
                .singleOrNull()
        }
        emit(latestLog)
    }

    /**
     * Lấy trạng thái MỚI NHẤT của TẤT CẢ các loại cổng thuộc về một trạm sạc.
     * @param connectionIds Danh sách các ID từ bảng Connections thuộc về một ChargePoint.
     * @return Một Flow chứa danh sách các bản ghi log cuối cùng cho mỗi connectionId.
     */
    fun getLiveStatusesForChargePoint(connectionIds: List<Int>): Flow<List<PortStatusLog>> = flow {
        val latestLogs = newSuspendedTransaction {
            if (connectionIds.isEmpty()) {
                emptyList()
            } else {
                val maxTimestampSubQuery = PortStatusLogs
                    // 1. Chỉ định các cột cần lấy ngay trong hàm select
                    .select(
                        PortStatusLogs.connectionId,
                        PortStatusLogs.simulationTimestamp.max().alias("max_timestamp") // Đặt bí danh cho cột max
                    )
                    // 2. Lọc ngay sau đó
                    .where { PortStatusLogs.connectionId inList connectionIds }
                    .groupBy(PortStatusLogs.connectionId)
                    .alias("max_ts") // Đặt bí danh cho toàn bộ subquery

                // Lấy bí danh của cột max_timestamp từ subquery
                val maxTimestampAlias = maxTimestampSubQuery[PortStatusLogs.simulationTimestamp.max().alias("max_timestamp")]

                // So sánh timestamp của bảng gốc với cột max_timestamp từ subquery
                PortStatusLogs
                    .innerJoin(
                        maxTimestampSubQuery,
                        onColumn = { PortStatusLogs.connectionId },
                        otherColumn = { maxTimestampSubQuery[PortStatusLogs.connectionId] }
                    )
                    .selectAll()
                    .where {
                        // So sánh timestamp của bảng gốc với cột max_timestamp từ subquery
                        (PortStatusLogs.simulationTimestamp eq maxTimestampAlias)
                    }
                    .map(::toPortStatusLog)
            }
        }
        emit(latestLogs)
    }
}
