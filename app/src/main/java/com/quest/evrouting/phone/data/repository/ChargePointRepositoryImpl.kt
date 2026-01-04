package com.quest.evrouting.phone.data.repository

import com.quest.evrouting.phone.data.remote.api.backendServer.staticc.ChargePointApiService
import com.quest.evrouting.phone.domain.model.ChargePoint
import com.quest.evrouting.phone.domain.repository.ChargePointRepository
import android.util.Log
import com.quest.evrouting.phone.data.remote.api.backendServer.BackendApiClient
import com.quest.evrouting.phone.data.remote.api.backendServer.staticc.toChargePoint

class ChargePointRepositoryImpl : ChargePointRepository {

    private val chargePointApiService = BackendApiClient.chargePointsService

    // Cache để lưu dữ liệu. Sử dụng Map<Int, ChargePoint> để tra cứu ID với hiệu năng O(1)
    @Volatile
    private var cachedChargePointsMap: Map<Int, ChargePoint>? = null

    /**
     * Lấy danh sách tất cả các trạm sạc.
     * Phiên bản này đơn giản hơn, không dùng Mutex.
     */
    override suspend fun getAllChargePoints(): List<ChargePoint> {
        // Nếu cache đã có, chỉ cần trả về danh sách các value của map.
        cachedChargePointsMap?.let {
            Log.d("ChargePointRepo", "Trả về dữ liệu trạm sạc từ cache.")
            return it.values.toList()
        }

        Log.d("ChargePointRepo", "Cache trống, gọi API để lấy dữ liệu trạm sạc.")
        return try {
            val apiDtoList = chargePointApiService.getAllChargePointInfo()
            Log.d("ChargePointRepo", "Đã lấy thành công ${apiDtoList.size} đối tượng từ API.")

            // Chuyển đổi từ List sang Map<Int, ChargePoint> và gán vào cache
            // associateBy là một hàm tiện ích tuyệt vời cho việc này.
            val domainMap = apiDtoList.map { it.toChargePoint() }.associateBy { it.id }
            cachedChargePointsMap = domainMap

            // Trả về danh sách các giá trị của map
            domainMap.values.toList()

        } catch (e: Exception) {
            Log.e("ChargePointRepo", "Lỗi khi lấy dữ liệu trạm sạc từ API: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Lấy thông tin một trạm sạc bằng ID, tận dụng hiệu năng của Map.
     */
    override suspend fun getChargePointById(id: Int): ChargePoint? {
        // Đảm bảo cache đã được điền dữ liệu.
        val currentCache = cachedChargePointsMap ?: getAllChargePoints().associateBy { it.id }.also { cachedChargePointsMap = it }

        // Truy cập trực tiếp từ Map, cực kỳ nhanh chóng.
        return currentCache[id]
    }
}
