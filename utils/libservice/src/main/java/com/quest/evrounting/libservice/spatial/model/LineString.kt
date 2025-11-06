package com.quest.evrounting.libservice.spatial.model

data class LineString(val coordinates: List<Point>) : Geometry {
//  Create with Point
    constructor(vararg coordinates: Point): this(coordinates.toList())

//  Create with Position
    constructor(coordinates: List<Position>): this(coordinates.map{
        Point(it)
    })
    constructor(vararg coordinates: Position): this(coordinates.toList())
}
