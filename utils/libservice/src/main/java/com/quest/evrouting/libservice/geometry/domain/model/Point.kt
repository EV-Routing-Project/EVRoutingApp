package com.quest.evrouting.libservice.geometry.domain.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.lang.IllegalArgumentException

/**
 * Represents the smallest unit of geometry, a single position in a coordinate system.
 * This class corresponds to the Point type in the GeoJSON specification.
 *
 * @property lon The longitude, measured in degrees. Must be within the range [-180, 180].
 * @property lat The latitude, measured in degrees. Must be within the range [-90, 90].
 * @property alt The altitude, measured in meters above sea level. Defaults to 0.0.
 * @throws IllegalArgumentException if longitude or latitude are outside of their valid range.
 */
@Serializable
data class Point(
    @Transient val lon: Double,
    @Transient val lat: Double,
    @Transient val alt: Double = 0.0
) : Geometry {
    //<editor-fold desc="Initialize and Constructors">
    init {
        require(lon >= -180.0 && lon <= 180.0) {
            "Invalid longitude: $lon. Longitude must be between -180 and 180."
        }
        require(lat >= -90.0 && lat <= 90.0) {
            "Invalid latitude: $lat. Latitude must be between -90 and 90."
        }
    }

    /**
     * Secondary constructor to create a Point from a list of coordinates.
     * The list must contain at least 2 (longitude, latitude) and at most 3 (longitude, latitude, altitude) elements.
     *
     * @param coordinates A list of doubles representing the coordinates.
     * @throws IllegalArgumentException if the coordinates list is not valid.
     */
    constructor(coordinates: List<Double>) : this(
        lon = coordinates.getOrElse(0) {
            throw IllegalArgumentException("Invalid coordinates: $coordinates. Longitude is missing.")
        },
        lat = coordinates.getOrElse(1) {
            throw IllegalArgumentException("Invalid coordinates: $coordinates. Latitude is missing.")
        },
        alt = coordinates.getOrNull(2) ?: 0.0
    ) {
        require(coordinates.size in 2..3) {
            "Invalid coordinates: $coordinates. Must contain between 2 and 3 elements, but has ${coordinates.size} elements."
        }
    }
    //</editor-fold>

    //<editor-fold desc="Static Properties">
    companion object {
        /**
         * The constant representing the GeoJSON type for a Point.
         */
        const val GEOMETRY_TYPE = "Point"
    }
    //</editor-fold>

    //<editor-fold desc="Override from Geometry">
    /**
     * The GeoJSON geometry type, which is always "Point".
     */
    override val type: String
        get() = GEOMETRY_TYPE
    //</editor-fold>

    //<editor-fold desc="Other Properties">
    /**
     * A list representation of the point's coordinates `[longitude, latitude, altitude]`.
     *
     * This property is computed lazily on the first access and the result is cached
     * for subsequent calls to improve performance.
     */
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    val coordinates: List<Double> by lazy {
        listOf(lon, lat, alt)
    }
    //</editor-fold>
}
