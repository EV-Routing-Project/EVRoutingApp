package com.quest.evrounting.algorithm.application.cache

import com.quest.evrounting.algorithm.domain.modelport.IPath
import com.quest.evrounting.algorithm.domain.modelport.IStation
import com.quest.evrounting.algorithm.domain.model.Point

class LineStringGraph : ICacheManager {
    private val nodeMap: MutableMap<Point, Node> = mutableMapOf()
    private val paths: MutableSet<IPath> = mutableSetOf()

    override fun insert(
        start: IStation,
        end: IStation,
        path: IPath
    ) {
        insert(start.point,end.point,path)
    }

    private fun insert(
        start: Point,
        end: Point,
        path: IPath
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
        start: IStation,
        end: IStation
    ): IPath? {
        return request(start.point, end.point)
    }

    private fun request(
        start: Point,
        end: Point
    ): IPath? {
        val startNode = nodeMap[start]
        val endNode = nodeMap[end]
        if(startNode == null || endNode == null){
            return null
        }
        return startNode.request(endNode)
    }

    override fun requestAll(): List<IPath> {
        return paths.toList()
    }

    override fun initWithOtherCache(otherCacheManager: ICacheManager) {
        for(path in otherCacheManager.requestAll()){
            this.insert(path.start, path.end, path)
        }
    }


    private class Node(
        val adj: MutableMap<Node, IPath> = mutableMapOf()
    ) {
        fun insert(node: Node, path: IPath){
            if(!this.adj.containsKey(node)){
                this.adj.put(node, path)
            }
        }

        fun request(node: Node): IPath? {
            if(this.adj.containsKey(node)){
                return adj[node]
            }
            return null
        }
    }
}