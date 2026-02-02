package com.quest.evrouting.phone.data.repository

import android.util.Log
import com.quest.evrouting.phone.data.remote.api.backendServer.BackendApiClient
import com.quest.evrouting.phone.data.remote.api.backendServer.staticc.toDomainPoi
import com.quest.evrouting.phone.domain.model.POI
import com.quest.evrouting.phone.domain.repository.PoiRepository

class PoiRepositoryImpl : PoiRepository {

    private val chargePointApiService = BackendApiClient.chargePointsService

    @Volatile
    private var cachedPoisMap: Map<String, POI>? = null

    override suspend fun getAllPois(): List<POI> {
        cachedPoisMap?.let {
            Log.d("PoiRepositoryImpl", "Trả về dữ liệu POI từ cache.")
            return it.values.toList()
        }

        Log.d("PoiRepositoryImpl", "Cache trống, gọi API để lấy dữ liệu POI.")
        return try {
            val apiResponse = chargePointApiService.getAllChargePointInfo()
            val apiDtoList = apiResponse.data

            Log.d("PoiRepositoryImpl", "Đã lấy thành công ${apiDtoList.size} đối tượng POI từ API.")

            val domainMap = apiDtoList.map { it.toDomainPoi() }.associateBy { it.id }
            cachedPoisMap = domainMap

            domainMap.values.toList()

        } catch (e: Exception) {
            Log.e("PoiRepositoryImpl", "Lỗi khi lấy dữ liệu POI từ API: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getPoiById(id: String): POI? {
        val currentCache = cachedPoisMap ?: getAllPois().associateBy { it.id }.also { cachedPoisMap = it }
        return currentCache[id]
    }
}
