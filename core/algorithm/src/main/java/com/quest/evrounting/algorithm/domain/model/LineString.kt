package com.quest.evrounting.algorithm.domain.model


/**
 * Represents a geometric LineString, defined by an ordered list of two or more points.
 * This class corresponds to the LineString type in the GeoJSON specification.
 *
 * @property coordinates The list of [Point]s that form the line.
 * @throws IllegalArgumentException if the coordinates list contains fewer than two points.
 */
data class LineString(val coordinates: List<Point>, val distance: Double = 0.0) : Geometry {
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
}

