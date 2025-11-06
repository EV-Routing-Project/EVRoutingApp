package com.quest.evrounting.libservice.geometry.model

data class Polygon(val edges: List<LineString>) : Geometry {
//  Create with LineString
    constructor(vararg edges: LineString): this(edges.toList())

//  Create with Point
    constructor(edges: List<List<Point>>): this(edges.map{
        LineString(it)
    })
    constructor(vararg edges: List<Point>): this(edges.toList())

//  Create with Position
    constructor(edges: List<List<Position>>) : this(edges.map{
        LineString(it)
    })
    constructor(vararg edges: List<Position>): this(edges.toList())
}
