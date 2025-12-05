package com.quest.evrouting.algorithm.infrastructure.adapter.tool.cache

import com.quest.evrouting.algorithm.domain.model.Point
import com.quest.evrouting.algorithm.domain.port.tool.cache.CacheServicePort
import com.quest.evrouting.algorithm.domain.port.model.PathPort
import com.quest.evrouting.algorithm.domain.port.model.StationPort

class GraphCacheServiceAdapter : CacheServicePort {
    private val nodeMap: MutableMap<Point, Node> = mutableMapOf()
    private val paths: MutableSet<PathPort> = mutableSetOf()

    override fun insert(
        start: StationPort,
        end: StationPort,
        path: PathPort
    ) {
        insert(start.getLocation(),end.getLocation(),path)
    }

    private fun insert(
        start: Point,
        end: Point,
        path: PathPort
    ) {
        if(!nodeMap.containsKey(start)){
            nodeMap.put(start, Node())
        }
        if(!nodeMap.containsKey(end)){
            nodeMap.put(end, Node())
        }
        val startNode = nodeMap[start]
        val endNode = nodeMap[end]
        if(startNode != null && endNode != null){
            startNode.insert(endNode, path)
            paths.add(path)
        }
    }


    override fun request(
        start: StationPort,
        end: StationPort
    ): PathPort? {
        return request(start.getLocation(), end.getLocation())
    }

    private fun request(
        start: Point,
        end: Point
    ): PathPort? {
        val startNode = nodeMap[start]
        val endNode = nodeMap[end]
        if(startNode == null || endNode == null){
            return null
        }
        return startNode.request(endNode)
    }

    override fun requestAll(): List<PathPort> {
        return paths.toList()
    }

    override fun initWithOtherCache(otherCacheManager: CacheServicePort) {
        for(path in otherCacheManager.requestAll()){
            if(path.isEmptyPath()) continue
            val start = path.getStartLocation()
            val end = path.getEndLocation()
            if(start != null && end != null){
                this.insert(start, end, path)
            }
        }
    }


    private class Node(
        val adj: MutableMap<Node, PathPort> = mutableMapOf()
    ) {
        fun insert(node: Node, path: PathPort){
            if(!this.adj.containsKey(node)){
                this.adj.put(node, path)
            }
        }

        fun request(node: Node): PathPort? {
            if(this.adj.containsKey(node)){
                return adj[node]
            }
            return null
        }
    }
}