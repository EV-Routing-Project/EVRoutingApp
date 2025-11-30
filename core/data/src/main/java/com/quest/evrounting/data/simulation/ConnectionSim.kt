package com.quest.evrounting.data.simulation

import com.quest.evrounting.data.model.staticc.AddressInfo
import kotlin.math.ln
import kotlin.random.Random


data class WeightedConnection(val connectionId: Int, val weight: Double)

enum class ConnectionStatus {
    AVAILABLE,
    OCCUPIED,
    MAINTENANCE,
    UNAVAILABLE
}

data class ConnectionSim(
    val id: Int,
    val powerKw: Double = 22.0,
    val addressInfo: AddressInfo, // Chứa thông tin Geohash
    var status: ConnectionStatus = ConnectionStatus.AVAILABLE
) {

    companion object {

        // geohashBits = 30 tương đương xác định một khu vực khoảng 0.6 km²
        // Trả ra kết quả từ 1-10
        private fun calculatePopularityByGeohash(
            allConnections: List<ConnectionSim>, geohashBits: Int = 30): Map<Int,Int> {
            if (geohashBits !in 1..60){
               throw IllegalArgumentException("Số bit Geohash phải nằm trong khoảng từ 1 đến 60.")
            }
            val bitsToShift = 60 - geohashBits
            val geohashGroupCount = allConnections.groupBy {
                it.addressInfo.geohash12 shr bitsToShift
            }.mapValues { it.value.size }

            return allConnections.associate { connect ->
                val geohashPrefixAsLong = connect.addressInfo.geohash12 shr bitsToShift
                val density = geohashGroupCount[geohashPrefixAsLong] ?: 1
                val score = 1 + ln(density.toDouble()).toInt()
                connect.id to score.coerceIn(1,10)
            }
        }

        /**
         * Tạo ra một danh sách các trạm sạc đã được gán "trọng số" để lựa chọn.
         */
        fun createWeightedList(connections: List<ConnectionSim>,
                               formula: ScoreFormula,
                               strategy: ChargingStrategy): Pair<List<WeightedConnection>, Double> {
            val popularityScores = calculatePopularityByGeohash(connections, 30)
            var totalWeight = 0.0
            val weightedList = connections.map { conn ->
                val popularityScore = popularityScores[conn.id] ?: 1
                val weight = formula.calculate(conn.powerKw, popularityScore, strategy)
                totalWeight += weight
                WeightedConnection(conn.id, weight)
            }
            return Pair(weightedList, totalWeight)
        }

        /**
         * Chọn một trạm sạc từ danh sách đã có trọng số.
         */
        fun selectByWeight(weightedConnections: List<WeightedConnection>, totalWeight: Double): Int {
            if (weightedConnections.isEmpty()) {
                throw IllegalStateException("Danh sách trạm sạc có trọng số không được rỗng.")
            }
            val randomValue = Random.nextDouble(0.0, totalWeight)
            var cumulativeWeight = 0.0
            for (conn in weightedConnections) {
                cumulativeWeight += conn.weight
                if (cumulativeWeight > randomValue) {
                    return conn.connectionId
                }
            }
            // Dự phòng, trả về trạm cuối cùng nếu có lỗi làm tròn số
            return weightedConnections.last().connectionId
        }
    }
}
