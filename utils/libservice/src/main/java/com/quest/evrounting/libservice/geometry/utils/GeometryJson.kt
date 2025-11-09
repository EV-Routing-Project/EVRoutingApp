package com.quest.evrounting.libservice.geometry.utils

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.quest.evrounting.libservice.geometry.domain.model.Geometry

/**
 * Encodes a [Geometry] object into its GeoJSON string representation.
 *
 * This extension function uses `kotlinx.serialization` to correctly handle
 * the polymorphism of the sealed [Geometry] interface.
 *
 * @return A JSON string representing the geometry, conforming to the GeoJSON standard.
 */
fun Geometry.toJson(): String {
    return Json.encodeToString(this)
}