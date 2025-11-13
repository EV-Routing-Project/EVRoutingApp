package com.quest.evrounting.libservice.geometry.domain.manager

import com.quest.evrounting.libservice.geometry.domain.port.PolylinePort
import com.quest.evrounting.libservice.geometry.domain.model.LineString

class PolylineManager(val polylineAdapter: PolylinePort){
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