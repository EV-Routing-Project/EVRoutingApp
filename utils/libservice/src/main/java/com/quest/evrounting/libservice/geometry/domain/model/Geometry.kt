package com.quest.evrounting.libservice.geometry.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a generic GeoJSON geometry object.
 *
 * This is a `sealed interface`, which means that all possible subtypes
 * (like [Point], [LineString], [Polygon], etc.) must be declared in the same package.
 * This allows the compiler to perform exhaustive checks in `when` expressions,
 * ensuring all geometry types are handled.
 *
 * @see <a href="https://tools.ietf.org/html/rfc7946#section-3.1">GeoJSON Specification</a>
 */
@Serializable
sealed interface Geometry {
    /**
     * The GeoJSON geometry type. For example, "Point", "LineString", or "Polygon".
     */
    val type: String
}