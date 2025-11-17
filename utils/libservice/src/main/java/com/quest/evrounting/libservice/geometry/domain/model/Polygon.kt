package com.quest.evrounting.libservice.geometry.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.lang.IllegalArgumentException

/**
 * Represents a geometric Polygon, defined by a list of rings (rings).
 * The first ring is the exterior boundary, and any subsequent rings are interior boundaries (holes).
 * This class corresponds to the Polygon type in the GeoJSON specification.
 *
 * According to GeoJSON rules:
 * - A polygon must have at least one ring.
 * - Each ring is a closed LineString, meaning the first and last points must be identical.
 * - Each ring must contain at least four points.
 *
 * @property rings A list of rings, where each ring is a list of [Point]s.
 * @throws IllegalArgumentException if the polygon's structure is invalid.
 */
@Serializable
data class Polygon(
    @SerialName("coordinates") val rings: List<List<Point>>
) : Geometry {
    //<editor-fold desc="Initialize and Constructors">
    init {
        require(rings.isNotEmpty()) { "A Polygon must have at least one ring." }
        rings.forEachIndexed { index, ring ->
            require(ring.size >= 4) {
                "Ring $index in Polygon must have at least 4 points, but found ${ring.size}."
            }
            require(ring.first() == ring.last()) {
                "Ring $index in Polygon is not a closed loop. The first and last points must be identical."
            }
        }
    }
//    /**
//     * Secondary constructor to create a Polygon from a list of [LineString] objects.
//     * This is useful when the rings are generated dynamically into a list.
//     *
//     * @param rings A list of [LineString]s forming the polygon's rings.
//     */
//    constructor(rings: List<LineString>) : this(rings.map { it.coordinates })
//
//    /**
//     * Secondary constructor to create a Polygon from a variable number of [LineString] arguments.
//     * This provides a more convenient way to build a Polygon from existing LineString objects.
//     *
//     * @param rings The sequence of [LineString]s forming the polygon's rings.
//     */
//    constructor(vararg rings: LineString) : this(rings.toList())
    //</editor-fold>

    //<editor-fold desc="Static Properties">
    companion object {
        /**
         * The constant representing the GeoJSON type for a Polygon.
         */
        const val GEOMETRY_TYPE = "Polygon"
    }
    //</editor-fold>

    //<editor-fold desc="Override from Geometry">
    /**
     * The GeoJSON geometry type, which is always "Polygon".
     */
    override val type: String
        get() = GEOMETRY_TYPE
    //</editor-fold>
}
