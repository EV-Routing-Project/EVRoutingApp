package com.quest.evrouting.libservice.geometry.domain.manager

import com.quest.evrouting.libservice.geometry.domain.port.PolylineServicePort
import com.quest.evrouting.libservice.geometry.domain.model.LineString

class PolylineManager(val polylineAdapter: PolylineServicePort){
    companion object{
        private const val PRECISION: Int = 6
    }

    fun decode(encodedPath: String): LineString? {
        return polylineAdapter.decode(encodedPath, PRECISION)
    }

    fun encode(lineString: LineString): String {
        return polylineAdapter.encode(lineString, PRECISION)
    }
}