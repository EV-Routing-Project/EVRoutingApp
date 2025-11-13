package com.quest.evrounting.libservice.geometry.infrastructure.adapter

import com.quest.evrounting.libservice.geometry.infrastructure.mapper.toDomain
import com.quest.evrounting.libservice.geometry.infrastructure.mapper.toExternal
import com.quest.evrounting.libservice.geometry.domain.port.PolylinePort
import com.quest.evrounting.libservice.geometry.domain.model.LineString
import com.mapbox.geojson.utils.PolylineUtils as ExternalPolylineUtils

class PolylineAdapter: PolylinePort {
    override fun decode(encodedPath: String, precision: Int): LineString? {
        val points = ExternalPolylineUtils.decode(encodedPath, precision).map {
            it.toDomain()
        }
        if(points.size < 2){
            return null
        }
        return LineString(points)
    }
    override fun encode(lineString: LineString, precision: Int): String {
        val path = lineString.coordinates.map {
            it.toExternal()
        }
        return ExternalPolylineUtils.encode(path,precision)
    }
}