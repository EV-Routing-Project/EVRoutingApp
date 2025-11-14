package com.quest.evrounting.libservice.geometry.service

import com.quest.evrounting.libservice.geometry.utils.toDomain
import com.quest.evrounting.libservice.geometry.utils.toMapbox
import com.quest.evrounting.libservice.geometry.model.LineString
import com.mapbox.geojson.utils.PolylineUtils

object GeoJsonService {
    private const val PRECISION: Int = 6
    /**
     * Decodes an encoded polyline string into a domain [LineString] object.
     *
     * A valid [LineString] requires at least two points. If the decoded polyline
     * results in fewer than two points, this function will return `null` to prevent
     * creating an invalid geometric object.
     *
     * @param encodedPath The polyline string to decode.
     * @return A valid [LineString] instance if decoding is successful and yields at least
     *         two points, otherwise `null`.
     */
    fun decodePolyline(encodedPath: String): LineString? {
        val points = PolylineUtils.decode(encodedPath, PRECISION).map{
            it.toDomain()
        }
        if(points.size < 2){
            return null
        }
        return LineString(points)
    }

    /**
     * Encodes a domain [LineString] object into a polyline string representation.
     *
     * @param lineString The [LineString] object to encode.
     * @return The resulting encoded polyline string.
     */
    fun encodePolyline(lineString: LineString): String {
        val path = lineString.coordinates.map {
            it.toMapbox()
        }
        return PolylineUtils.encode(path,PRECISION)
    }
}