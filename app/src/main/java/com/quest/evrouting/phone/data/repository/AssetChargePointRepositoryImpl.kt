//package com.quest.evrouting.phone.data.repository
//
//import android.content.Context
//import com.mapbox.geojson.Feature
//import com.mapbox.geojson.FeatureCollection
//import com.mapbox.geojson.Point
//import com.quest.evrouting.phone.domain.model.ChargePoint
//import com.quest.evrouting.phone.domain.repository.ChargePointRepository
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//
///**
// * Lớp triển khai cụ thể của ChargePointRepository.
// * Lấy dữ liệu từ một file GeoJSON nằm trong thư mục assets.
// */
//class AssetChargePointRepositoryImpl(
//    private val context: Context // Yêu cầu Hilt cung cấp Application Context
//) : ChargePointRepository { // "Ký" hợp đồng với ChargePointRepository
//
//    override suspend fun getAllChargePoints(): List<ChargePoint> {
//        // Thực hiện các tác vụ nặng (I/O) trên luồng IO để không chặn Main thread
//        return withContext(Dispatchers.IO) {
//            try {
//                // Đọc nội dung file GeoJSON từ thư mục assets
//                val geoJsonString = context.assets.open("ChargePoint.geojson").bufferedReader().use { it.readText() }
//
//                // Phân tích cú pháp chuỗi JSON thành đối tượng FeatureCollection của Mapbox
//                val featureCollection = FeatureCollection.fromJson(geoJsonString)
//
//                // Chuyển đổi (map) từ danh sách Feature thành danh sách ChargePoint (Model sạch)
//                featureCollection.features()?.mapNotNull { feature ->
//                    // Chỉ xử lý những feature có geometry là Point
//                    (feature.geometry() as? Point)?.let { point ->
//                        feature.toChargePoint(point)
//                    }
//                } ?: emptyList() // Nếu features() là null, trả về danh sách rỗng
//
//            } catch (e: Exception) {
//                // Nếu có bất kỳ lỗi nào (ví dụ: file không tìm thấy, JSON sai định dạng),
//                // ném ra một ngoại lệ để lớp gọi (UseCase/ViewModel) có thể xử lý.
//                e.printStackTrace() // In lỗi ra logcat để debug
//                throw IllegalStateException("Failed to parse ChargePoint.geojson", e)
//            }
//        }
//    }
//
//    override suspend fun getChargePointById(id: Int): ChargePoint? {
//        // Cách 1: Nếu bạn có thể gọi API trực tiếp
//         return chargePointApi.getById(id)
//
//        // Cách 2: Tìm kiếm từ danh sách đầy đủ (kém hiệu quả hơn nhưng đơn giản)
//        return getChargePoints().find { it.id == id }
//    }
//
//
//
//    /**
//     * Một hàm mở rộng (extension function) tiện ích để chuyển đổi một Feature
//     * thành một đối tượng ChargePoint.
//     */
//    private fun Feature.toChargePoint(point: Point): ChargePoint {
//        val properties = this.properties()
//        val id = properties?.get("id")?.asInt ?: -1
//        val name = properties?.get("name")?.asString ?: "Unknown Name"
//        val quantity = properties?.get("quantity")?.asInt ?: 0
//
//        return ChargePoint(
//            id = id,
//            name = name,
//            quantity = quantity,
//            point = point
//        )
//    }
//}
