package com.quest.evrouting.phone.domain.repository

import com.mapbox.geojson.Point

interface GeocodingRepository {
    suspend fun getCoordinatesForPlaceName(placeName: String): Point?
}
