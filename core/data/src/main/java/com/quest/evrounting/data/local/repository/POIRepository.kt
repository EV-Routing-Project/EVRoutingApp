package com.quest.evrounting.data.local.repository

import com.quest.evrounting.data.model.dynamic.*
import com.quest.evrounting.data.model.staticc.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


/**
 * Repository quản lý toàn bộ dữ liệu liên quan đến một trạm sạc (POI),
 * bao gồm thông tin chính (ChargePoint), địa chỉ (AddressInfo) và các cổng sạc (Connections).
 */
object POIRepository {

    //region --- HÀM CHUYỂN ĐỔI (CONVERTERS) ---
    private fun toChargePoint(row: ResultRow): ChargePoint = ChargePoint(
        id = row[ChargePoints.id],
        uuid = row[ChargePoints.uuid],
        addressInfoId = row[ChargePoints.addressInfoId],
        operatorId = row[ChargePoints.operatorId],
        usageTypeId = row[ChargePoints.usageTypeId],
    )

    private fun toConnection(row: ResultRow): Connection = Connection(
        id = row[Connections.id],
        chargePointId = row[Connections.chargePointId],
        connectionTypeId = row[Connections.connectionTypeId],
        currentTypeId = row[Connections.currentTypeId],
        powerKw = row[Connections.powerKw],
        quantity = row[Connections.quantity]
    )

    private fun toAddressInfo(row: ResultRow): AddressInfo = AddressInfo(
        id = row[AddressInfos.id],
        title = row[AddressInfos.title],
        addressLine1 = row[AddressInfos.addressLine1],
        town = row[AddressInfos.town],
        postcode = row[AddressInfos.postcode],
        countryId = row[AddressInfos.countryId],
        latitude = row[AddressInfos.latitude],
        longitude = row[AddressInfos.longitude],
        accessComments = row[AddressInfos.accessComments],
        geohash12 = row[AddressInfos.geohash12]
    )
    //endregion

    //region --- HÀM CHÈN/CẬP NHẬT (UPSERT) ---

    /**
     * Chèn hoặc cập nhật một danh sách các POI đầy đủ.
     * Hàm này sẽ xử lý cả 3 bảng: ChargePoint, AddressInfo, và Connections.
     */
    suspend fun upsertFullPOIs(
        chargePoints: List<ChargePoint>,
        addressInfos: List<AddressInfo>,
        connections: List<Connection>
    ) {
        newSuspendedTransaction<Unit> {
            AddressInfos.batchUpsert(addressInfos) { address ->
                this[AddressInfos.id] = address.id
                this[AddressInfos.title] = address.title
                this[AddressInfos.addressLine1] = address.addressLine1
                this[AddressInfos.town] = address.town
                this[AddressInfos.postcode] = address.postcode
                this[AddressInfos.countryId] = address.countryId
                this[AddressInfos.latitude] = address.latitude
                this[AddressInfos.longitude] = address.longitude
                this[AddressInfos.accessComments] = address.accessComments
                this[AddressInfos.geohash12] = address.geohash12
            }

            ChargePoints.batchUpsert(chargePoints) { chargePoint ->
                this[ChargePoints.id] = chargePoint.id
                this[ChargePoints.uuid] = chargePoint.uuid
                this[ChargePoints.addressInfoId] = chargePoint.addressInfoId
                this[ChargePoints.operatorId] = chargePoint.operatorId
                this[ChargePoints.usageTypeId] = chargePoint.usageTypeId
            }

            Connections.batchUpsert(connections) { connection ->
                this[Connections.id] = connection.id
                this[Connections.chargePointId] = connection.chargePointId
                this[Connections.connectionTypeId] = connection.connectionTypeId
                this[Connections.currentTypeId] = connection.currentTypeId
                this[Connections.powerKw] = connection.powerKw
                this[Connections.quantity] = connection.quantity
            }

            // Khởi tạo trạng thái ban đầu trong PortStatusLog
            val initialLogs = connections.map { conn ->
                PortStatusLog(
                    logId = 0, // Sẽ được tự động tạo
                    connectionId = conn.id,
                    // Ban đầu, tất cả các cổng đều còn trống
                    availablePorts = conn.quantity ?: 1,
                    // Mốc thời gian bắt đầu mô phỏng
                    simulationTimestamp = 0L
                )
            }

            // Chèn các bản ghi log ban đầu
            if (initialLogs.isNotEmpty()) {
                PortStatusLogs.batchInsert(initialLogs) { log ->
                    this[PortStatusLogs.connectionId] = log.connectionId
                    this[PortStatusLogs.availablePorts] = log.availablePorts
                    this[PortStatusLogs.simulationTimestamp] = log.simulationTimestamp
                }
            }
        }
    }
    //endregion

    //region --- HÀM TRUY VẤN (GET) ---

    // --- Truy vấn ChargePoint ---
    fun getAllChargePoints(): Flow<List<ChargePoint>> = flow {
        val items = newSuspendedTransaction {
            ChargePoints.selectAll().map(::toChargePoint)
        }
        emit(items)
    }

    fun getChargePointById(id: Int): Flow<ChargePoint?> = flow {
        val item = newSuspendedTransaction {
            ChargePoints.selectAll().where { ChargePoints.id eq id }
                .limit(1)
                .map(::toChargePoint)
                .singleOrNull()
        }
        emit(item)
    }

    // --- Truy vấn Connection ---
    fun getConnectionsForChargePoint(chargePointId: Int): Flow<List<Connection>> = flow {
        val items = newSuspendedTransaction {
            Connections.selectAll().where { Connections.chargePointId eq chargePointId }
                .map(::toConnection)
        }
        emit(items)
    }

    fun getConnectionById(id: Int): Flow<Connection?> = flow {
        val item = newSuspendedTransaction {
            Connections.selectAll().where { Connections.id eq id }
                .limit(1)
                .map(::toConnection)
                .singleOrNull()
        }
        emit(item)
    }


    // --- Truy vấn AddressInfo ---
    fun getAddressInfoById(id: Int): Flow<AddressInfo?> = flow {
        val item = newSuspendedTransaction {
            AddressInfos.selectAll().where { AddressInfos.id eq id }
                .limit(1)
                .map(::toAddressInfo)
                .singleOrNull()
        }
        emit(item)
    }

    //endregion
}
