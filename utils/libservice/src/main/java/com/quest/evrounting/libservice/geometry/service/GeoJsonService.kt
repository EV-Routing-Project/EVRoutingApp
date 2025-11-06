package com.quest.evrounting.libservice.geometry.service

import com.quest.evrounting.libservice.geometry.utils.toDomain
import com.quest.evrounting.libservice.geometry.utils.toKotlin
import com.quest.evrounting.libservice.geometry.model.LineString as Polyline
import de.brudaswen.kotlin.polyline.PolylineEncoding

object GeoJsonService {
    fun decodePolyline(encodePolyline: String): Polyline {
        return PolylineEncoding.decode(encodePolyline).toDomain()
    }
    fun encodePolyline(polyline: Polyline): String {
        return PolylineEncoding.encode(polyline.toKotlin())
    }
}