package com.quest.evrouting.data.local.repository

import com.quest.evrouting.data.model.dynamic.*
import com.quest.evrouting.data.model.staticc.*
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


            PortStatusRepository.clearAllLogs()
            // Khởi tạo trạng thái ban đầu trong PortStatusLog
            // (Trạng thái cho từng Connections ban đầu)
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
    suspend fun getAllChargePoints(): List<ChargePoint> {
        return newSuspendedTransaction {
            ChargePoints.selectAll().map(::toChargePoint)
        }
    }

    suspend fun getAllChargePointIDs(): List<Int> {
        return newSuspendedTransaction {
            ChargePoints.selectAll().map { it[ChargePoints.id] }
        }
    }

    suspend fun getChargePointById(id: Int): ChargePoint? {
        return newSuspendedTransaction {
            ChargePoints.selectAll().where { ChargePoints.id eq id }
                .limit(1)
                .map(::toChargePoint)
                .singleOrNull()
        }
    }

    // --- Truy vấn Connection ---
    suspend fun getTotalPortCount(): Int {
        return newSuspendedTransaction {
            Connections.selectAll().sumOf { it[Connections.quantity] ?: 0 }
        }
    }

    suspend fun getConnectionsForChargePoint(chargePointId: Int): List<Connection> {
        return newSuspendedTransaction {
            Connections.selectAll().where { Connections.chargePointId eq chargePointId }
                .map(::toConnection)
        }
    }

    suspend fun getAllConnections(): List<Connection>{
        return newSuspendedTransaction {
            Connections.selectAll().map(::toConnection)
        }
    }

    suspend fun getAllConnetionIDs(): List<Int> {
        return newSuspendedTransaction {
            Connections.selectAll().map { it[Connections.id] }
        }
    }

    suspend fun getConnectionById(id: Int): Connection? {
        return newSuspendedTransaction {
            Connections.selectAll().where { Connections.id eq id }
                .limit(1)
                .map(::toConnection)
                .singleOrNull()
        }
    }


    // --- Truy vấn AddressInfo ---
    suspend fun getAddressInfoById(id: Int): AddressInfo? {
        return newSuspendedTransaction {
            AddressInfos.selectAll().where { AddressInfos.id eq id }
                .limit(1)
                .map(::toAddressInfo)
                .singleOrNull()
        }
    }

    suspend fun getAddressInfoForChargePoint(chargePointId: Int): AddressInfo? {
        return newSuspendedTransaction {
            (ChargePoints innerJoin AddressInfos)
                .selectAll()
                .where { ChargePoints.id eq chargePointId }
                .limit(1)
                .map(::toAddressInfo)
                .singleOrNull()
        }
    }

    //endregion
}
