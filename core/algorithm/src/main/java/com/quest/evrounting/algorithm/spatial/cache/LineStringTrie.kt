package com.quest.evrounting.algorithm.spatial.cache

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.port.GeometryPort
import com.quest.evrounting.algorithm.spatial.entity.BaseEntity

class LineStringTrie(
    val geometryTool: GeometryPort
) : ICacheManager {

}