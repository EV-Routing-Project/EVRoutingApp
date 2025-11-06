package com.quest.evrounting.libservice.spatial.model

data class Point(val coordinates: Position) : Geometry {
//  Create with Position
    constructor(longitude: Double, latitude: Double) : this(Position(longitude, latitude))
}
