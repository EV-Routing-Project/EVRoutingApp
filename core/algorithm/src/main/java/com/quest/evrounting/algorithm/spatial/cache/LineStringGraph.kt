package com.quest.evrounting.algorithm.spatial.cache

import com.quest.evrounting.algorithm.domain.model.LineString
import com.quest.evrounting.algorithm.domain.model.Point
import com.quest.evrounting.algorithm.spatial.entity.BaseEntity

class LineStringGraph : ICacheManager {
    private val entityMap: MutableMap<Point, Node> = mutableMapOf()

    override fun insert(
        start: BaseEntity,
        end: BaseEntity,
        path: LineString
    ) {
        if(!entityMap.containsKey(start.point)){
            entityMap.put(start.point, Node())
        }
        if(!entityMap.containsKey(end.point)){
            entityMap.put(end.point, Node())
        }
        val startNode = entityMap[start.point]
        val endNode = entityMap[end.point]
        if(startNode != null && endNode != null){
            startNode.insert(endNode, path)
        }
    }

    override fun request(
        start: BaseEntity,
        end: BaseEntity
    ): LineString? {
        val startNode = entityMap[start.point]
        val endNode = entityMap[end.point]
        if(startNode == null || endNode == null){
            return null
        }
        return startNode.request(endNode)
    }


    private class Node(
        val adj: MutableMap<Node, LineString> = mutableMapOf()
    ) {
        fun insert(node: Node, path: LineString){
            if(!this.adj.containsKey(node)){
                this.adj.put(node, path)
            }
        }

        fun request(node: Node): LineString? {
            if(this.adj.containsKey(node)){
                return adj[node]
            }
            return null
        }
    }
}