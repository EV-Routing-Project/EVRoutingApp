package com.quest.evrouting.libservice.geometry.domain.model

import kotlinx.serialization.Serializable


/**
 * Represents a geometric LineString, defined by an ordered list of two or more points.
 * This class corresponds to the LineString type in the GeoJSON specification.
 *
 * @property coordinates The list of [Point]s that form the line.
 * @throws IllegalArgumentException if the coordinates list contains fewer than two points.
 */
@Serializable
data class LineString(val coordinates: List<Point>) : Geometry {
    //<editor-fold desc="Initialize and Constructors">
    init {
        require(coordinates.size >= 2){
            "Invalid coordinates: $coordinates. LineString must have at least 2 points."
        }
    }

    /**
     * Secondary constructor to create a LineString from a variable number of [Point] arguments.
     * This provides a more convenient way to instantiate the class than creating a list manually.
     *
     * Example: `LineString(point1, point2, point3)`
     *
     * @param coordinates The sequence of [Point]s forming the line.
     */
    constructor(vararg coordinates: Point): this(coordinates.toList())
    //</editor-fold>

    //<editor-fold desc="Static Properties">
    companion object {
        /**
         * The constant representing the GeoJSON type for a LineString.
         */
        const val GEOMETRY_TYPE = "LineString"
    }
    //</editor-fold>

    //<editor-fold desc="Override from Geometry">
    /**
     * The GeoJSON geometry type, which is always "LineString".
     */
    override val type: String
        get() = GEOMETRY_TYPE
    //</editor-fold>
}
