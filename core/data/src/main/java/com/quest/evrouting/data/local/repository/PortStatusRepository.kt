package com.quest.evrouting.data.local.repository

import com.quest.evrouting.data.model.dynamic.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


object PortStatusRepository {

    private fun toPortStatusLog(row: ResultRow): PortStatusLog = PortStatusLog(
        logId = row[PortStatusLogs.logId],
        connectionId = row[PortStatusLogs.connectionId],
        availablePorts = row[PortStatusLogs.availablePorts],
        simulationTimestamp = row[PortStatusLogs.simulationTimestamp]
    )

    /**
     * Ghi một sự kiện thay đổi số lượng cổng trống vào log (không sử dụng cho
     * trạng thái khởi tạo ban đầu)
     */
    suspend fun insertNewState(logEntry: PortStatusLog) {
        newSuspendedTransaction<Unit> {
            PortStatusLogs.insert {
                // logId sẽ được tự động tạo bởi cơ sở dữ liệu nên không cần điền logId
                it[connectionId] = logEntry.connectionId
                it[availablePorts] = logEntry.availablePorts
                it[simulationTimestamp] = logEntry.simulationTimestamp
            }
        }
    }


    suspend fun insertNewStateForAll(logEntries: List<PortStatusLog>) {
        if (logEntries.isEmpty()) {
            return
        }
        newSuspendedTransaction {
            PortStatusLogs.batchInsert(logEntries, shouldReturnGeneratedValues = false) { logEntry ->
                this[PortStatusLogs.connectionId] = logEntry.connectionId
                this[PortStatusLogs.availablePorts] = logEntry.availablePorts
                this[PortStatusLogs.simulationTimestamp] = logEntry.simulationTimestamp
            }
        }
    }


    // Hàm tìm kiếm trạng thái cho tất cả Connections
    suspend fun getLatestStatusForAllConnections(atTimestamp: Long): List<PortStatusLog> {
        return newSuspendedTransaction {
//            val fiveMinutesInMillis = 5 * 60 * 1000L
//            val roundedTimestamp = (atTimestamp / fiveMinutesInMillis) * fiveMinutesInMillis

            // Nếu không có thì tìm mốc nhỏ hơn
            val closestTimestamp = PortStatusLogs
                .select(PortStatusLogs.simulationTimestamp)
                .where { PortStatusLogs.simulationTimestamp lessEq atTimestamp }
                .orderBy(PortStatusLogs.simulationTimestamp, SortOrder.DESC)
                .limit(1)
                .singleOrNull()
                ?.get(PortStatusLogs.simulationTimestamp)

            if (closestTimestamp != null) {
                PortStatusLogs
                    .selectAll()
                    .where { PortStatusLogs.simulationTimestamp eq closestTimestamp }
                    .map(::toPortStatusLog)
            } else {
                emptyList()
            }
        }
    }


    /**
     * Lấy trạng thái MỚI NHẤT (live status) của một loại cổng sạc (Connection).
     * @return Một Flow chứa bản ghi log cuối cùng, hoặc null nếu chưa có.
     */
    fun getLatestStatus(connectionId: Int): Flow<PortStatusLog?> = flow {
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
     * Lấy trạng thái gần nhất của một nhóm cổng sạc TÍNH ĐẾN một thời điểm cụ thể.
     * @return Một đối tượng PortStatusLog chứa trạng thái gần nhất, hoặc null nếu không tìm thấy.
     */
    suspend fun getLatestStatus(connectionId: Int, atTimestamp: Long): PortStatusLog? {
        return newSuspendedTransaction {
            PortStatusLogs
                .selectAll()
                .where {
                    (PortStatusLogs.connectionId eq connectionId) and
                            (PortStatusLogs.simulationTimestamp lessEq atTimestamp)
                }
                .orderBy(PortStatusLogs.simulationTimestamp, SortOrder.DESC)
                .limit(1)
                .map(::toPortStatusLog)
                .singleOrNull()
        }
    }


    /**
     * Lấy trạng thái của TẤT CẢ các loại cổng thuộc một trạm sạc TẠI MỘT THỜI ĐIỂM CỤ THỂ.
     * @param connectionIds Danh sách các ID từ bảng Connections thuộc về một ChargePoint.
     * @param atTimestamp Thời điểm trong quá khứ mà bạn muốn truy vấn trạng thái.
     * @return Một danh sách các bản ghi log gần nhất (nhưng không muộn hơn atTimestamp) cho mỗi connectionId.
     */
    suspend fun getStatusesAtTimestampForConnections(connectionIds: List<Int>, atTimestamp: Long): List<PortStatusLog> {
        return newSuspendedTransaction {
            if (connectionIds.isEmpty()) {
                emptyList()
            } else {
                // Bước 1: Tạo truy vấn con để tìm timestamp lớn nhất NHƯNG KHÔNG VƯỢT QUÁ atTimestamp cho mỗi connectionId.
                val maxTimestampSubQuery = PortStatusLogs
                    .select(
                        PortStatusLogs.connectionId,
                        PortStatusLogs.simulationTimestamp.max().alias("max_timestamp")
                    )
                    .where {
                        (PortStatusLogs.connectionId inList connectionIds) and
                                (PortStatusLogs.simulationTimestamp lessEq atTimestamp) // Điều kiện quan trọng được thêm vào
                    }
                    .groupBy(PortStatusLogs.connectionId)
                    .alias("max_ts")

                val maxTimestampAlias = maxTimestampSubQuery[PortStatusLogs.simulationTimestamp.max().alias("max_timestamp")]

                // Bước 2: Join bảng gốc với kết quả của truy vấn con.
                // Logic join và lọc cuối cùng không thay đổi.
                PortStatusLogs
                    .innerJoin(
                        maxTimestampSubQuery,
                        onColumn = { PortStatusLogs.connectionId },
                        otherColumn = { maxTimestampSubQuery[PortStatusLogs.connectionId] }
                    )
                    .selectAll()
                    .where {
                        (PortStatusLogs.simulationTimestamp eq maxTimestampAlias)
                    }
                    .map(::toPortStatusLog)
            }
        }
    }


    // hàm này sẽ giữ lại trạng thái khởi tạo
    suspend fun clearSimulationLogs() {
        newSuspendedTransaction {
            PortStatusLogs.deleteWhere { PortStatusLogs.simulationTimestamp greater 0L }
        }
    }


    suspend fun clearAllLogs() {
        newSuspendedTransaction {
            PortStatusLogs.deleteAll()
        }
    }
}
