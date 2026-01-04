package com.quest.evrouting.phone.data.repository

import android.util.Log
import com.mapbox.geojson.Point
import com.quest.evrouting.phone.BuildConfig // << 1. IMPORT BUILDCONFIG
import com.quest.evrouting.phone.data.remote.api.mapbox.MapboxApiClient
import com.quest.evrouting.phone.domain.repository.GeocodingRepository

/**
 * Lớp triển khai cụ thể cho GeocodingRepository.
 * Nhiệm vụ của nó là gọi đến Mapbox Geocoding API để chuyển đổi tên địa điểm thành tọa độ.
 */
class GeocodingRepositoryImpl : GeocodingRepository {

    // Lấy service từ ApiClient đã cấu hình
    private val geocodingApiService = MapboxApiClient.geocodingService

    /**
     * Triển khai hàm tìm tọa độ.
     */
    override suspend fun getCoordinatesForPlaceName(placeName: String): Point? {
        Log.d("DEBUG_GEOCODING", "[Repository] Bắt đầu geocoding cho: '$placeName'")

        try {
            // 2. GỌI API THÔNG QUA RETROFIT SERVICE
            val response = geocodingApiService.getCoordinates(
                searchText = placeName,
                accessToken = BuildConfig.MAPBOX_ACCESS_TOKEN // <-- Lấy token từ BuildConfig
            )

            // 3. XỬ LÝ KẾT QUẢ TRẢ VỀ
            // Lấy tọa độ từ `feature` đầu tiên trong danh sách kết quả
            val coordinates = response.features.firstOrNull()?.center

            // Mapbox trả về một List<Double> với thứ tự [longitude, latitude]
            return if (coordinates != null && coordinates.size >= 2) {
                val longitude = coordinates[0]
                val latitude = coordinates[1]
                Log.d("DEBUG_GEOCODING", "[Repository] Tìm thấy tọa độ cho '$placeName': Lon=$longitude, Lat=$latitude")
                Point.fromLngLat(longitude, latitude)
            } else {
                Log.w("DEBUG_GEOCODING", "[Repository] Không tìm thấy tọa độ cho '$placeName'")
                null // Trả về null nếu không có kết quả hoặc kết quả không hợp lệ
            }

        } catch (e: Exception) {
            Log.e("DEBUG_GEOCODING", "[Repository] Lỗi khi đang geocoding cho '$placeName'", e)
            throw e // Ném lại lỗi để ViewModel có thể bắt và xử lý
        }
    }
}
