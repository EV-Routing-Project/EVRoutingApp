package com.quest.evrounting.data.local.database

import com.quest.evrounting.data.local.repository.POIRepository
import com.quest.evrounting.data.local.repository.ReferenceRepository
import com.quest.evrounting.data.mapper.ToEntity.toEntity
import com.quest.evrounting.data.remote.OcmApiCaller
import kotlinx.coroutines.runBlocking

object DataIngestionService {
    private const val API_KEY = "KEY"

    suspend fun syncData() {
        println("🚀 Bắt đầu quá trình tạo schema...")
        DatabaseFactory.createSchema()

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
            // Chúng ta dùng `flatMap` để "làm phẳng" cấu trúc từ List<List<ConnectionEntity>> thành List<ConnectionEntity>.
            // flatMap gộp tất cả danh sách lại thành 1 danh sách lớn duy nhất
            val connectionEntities = poisList.flatMap { chargePointApi ->
                // Với mỗi trạm sạc, lấy danh sách các cổng kết nối của nó
                // và chuyển đổi từng cổng, đồng thời gán `chargePointId`
                chargePointApi.connections.map { connectionApi ->
                    connectionApi.toEntity(chargePointId = chargePointApi.id)
                }
            }

            val connectionTypeEntities = poisList.flatMap { it.connections }
                .map { it.connectionType.toEntity() }
                .distinct()

            val currentTypeEntities = poisList.flatMap { it.connections }
                .mapNotNull { it.currentType?.toEntity() }
                .distinct()

            println("🗺️ Đã map thành công ${chargePointEntities.size} POIs. Bắt đầu lưu vào database...")

            // Lưu dữ liệu bảng tham chiếu vào database
            ReferenceRepository.upsertCountries(poisList.map { it.addressInfo.country.toEntity() }.distinct())
            ReferenceRepository.upsertOperators(poisList.map { it.operatorInfo.toEntity() }.distinct())
            ReferenceRepository.upsertUsageTypes(poisList.map { it.usageType.toEntity() }.distinct())
            ReferenceRepository.upsertConnectionTypes(connectionTypeEntities)
            ReferenceRepository.upsertCurrentTypes(currentTypeEntities)

            // Lưu dữ liệu bảng tĩnh chính vào database
            POIRepository.upsertFullPOIs(
                chargePoints = chargePointEntities,
                addressInfos = addressInfoEntities,
                connections = connectionEntities
            )

            println("💾✅ Đã đồng bộ và lưu dữ liệu vào database thành công.")

        }.onFailure { exception ->
            println("Repository: Không thể đồng bộ dữ liệu do lỗi: ${exception.message}")
        }
    }
}

fun main() = runBlocking {
    println("==============================================")
    println(" BẮT ĐẦU CHẠY DATA INGESTION SERVICE ")
    println("==============================================")

    try {
        DataIngestionService.syncData()
    } catch (e: Exception) {
        println("🚨 Đã xảy ra lỗi không mong muốn ở tầng cao nhất.")
        e.printStackTrace()
    }

    println("==============================================")
    println(" KẾT THÚC QUÁ TRÌNH CHẠY ")
    println("==============================================")
}