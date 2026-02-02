package com.quest.evrouting.phone.data.repository

import android.util.Log
import com.mapbox.geojson.Point
import com.quest.evrouting.phone.BuildConfig
import com.quest.evrouting.phone.data.remote.api.mapbox.MapboxApiClient
import com.quest.evrouting.phone.domain.repository.GeocodingRepository

class GeocodingRepositoryImpl : GeocodingRepository {

    private val geocodingApiService = MapboxApiClient.geocodingService

    override suspend fun getCoordinatesForPlaceName(placeName: String): Point? {
        Log.d("DEBUG_GEOCODING", "[Repository] Bắt đầu geocoding cho: '$placeName'")

        try {
            val response = geocodingApiService.getCoordinates(
                searchText = placeName,
                accessToken = BuildConfig.MAPBOX_ACCESS_TOKEN
            )

            val coordinates = response.features.firstOrNull()?.center

            // Mapbox trả về một List<Double> với thứ tự [longitude, latitude]
            return if (coordinates != null && coordinates.size >= 2) {
                val longitude = coordinates[0]
                val latitude = coordinates[1]
                Log.d("DEBUG_GEOCODING", "[Repository] Tìm thấy tọa độ cho '$placeName': Lon=$longitude, Lat=$latitude")
                Point.fromLngLat(longitude, latitude)
            } else {
                Log.w("DEBUG_GEOCODING", "[Repository] Không tìm thấy tọa độ cho '$placeName'")
                null
            }

        } catch (e: Exception) {
            Log.e("DEBUG_GEOCODING", "[Repository] Lỗi khi đang geocoding cho '$placeName'", e)
            throw e
        }
    }
}
