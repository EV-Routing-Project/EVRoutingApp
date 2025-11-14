package com.quest.evrounting.data.local.database

import com.quest.evrounting.data.local.repository.POIRepository
import com.quest.evrounting.data.mapper.ToEntity.toEntity
import com.quest.evrounting.data.remote.OcmApiCaller

object DataIngestionService {
    private const val API_KEY = "KEY"

    suspend fun syncData() {
        println("🚀 Bắt đầu quá trình đồng bộ dữ liệu từ OCM...")
        // Gọi hàm mới và nhận về một đối tượng Result
        val result = OcmApiCaller.fetchChargePoints(API_KEY)

        result.onSuccess { poisList ->
            println("✅ Nhận được ${poisList.size} trạm sạc. Bắt đầu quá trình mapping...")

            // Chuyển đổi danh sách các trạm sạc (ChargePoint)
            val chargePointEntities = poisList.map { it.toEntity() }

            // Chuyển đổi danh sách các thông tin địa chỉ (AddressInfo)
            val addressInfoEntities = poisList.map { chargePointApi ->
                chargePointApi.addressInfo.toEntity()
            }

            // Chuyển đổi danh sách các cổng kết nối (Connection)
            // Đây là một phép biến đổi phức tạp hơn vì mỗi trạm sạc có một *danh sách* các cổng kết nối.
            // Chúng ta dùng `flatMap` để "làm phẳng" cấu trúc từ List<List<ConnectionEntity>> thành List<ConnectionEntity>.
            val connectionEntities = poisList.flatMap { chargePointApi ->
                // Với mỗi trạm sạc, lấy danh sách các cổng kết nối của nó
                // và chuyển đổi từng cổng, đồng thời gán `chargePointId`
                chargePointApi.connections.map { connectionApi ->
                    connectionApi.toEntity(chargePointId = chargePointApi.id)
                }
            }
            println("🗺️ Đã map thành công ${chargePointEntities.size} POIs. Bắt đầu lưu vào database...")

            POIRepository.upsertFullPOIs(
                chargePoints = chargePointEntities,
                addressInfos = addressInfoEntities,
                connections = connectionEntities
            )

            println("💾✅ Đã đồng bộ và lưu dữ liệu vào database thành công thông qua Repository.")

        }.onFailure { exception ->
            println("Repository: Không thể đồng bộ dữ liệu do lỗi: ${exception.message}")
        }
    }

}