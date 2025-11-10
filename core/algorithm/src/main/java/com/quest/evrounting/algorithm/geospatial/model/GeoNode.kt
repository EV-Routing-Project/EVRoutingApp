package com.quest.evrounting.algorithm.geospatial.model

import com.quest.evrounting.algorithm.geospatial.utils.GeoUtils

class GeoNode<T>(val parent: GeoNode<T>? = null) {
    val level: Int
    val isEnd: Boolean
    val children: Array<GeoNode<T>?>
    val cnt: Array<Int>
    val leafs: MutableSet<GeoLeaf<T>>?
    init {
        if (parent != null) {
            level = parent.level - 1
            if (level == 0) {
                isEnd = true
            } else {
                isEnd = false
            }
        } else {
            level = GeoUtils.MAX_LEVEL
            isEnd = false
        }
        if(isEnd){
            children = arrayOfNulls(0)
            cnt = arrayOf(0)
        } else {
            children = arrayOfNulls(2)
            cnt = arrayOf(0, 0)
        }
        if(isEnd or (level == GeoUtils.GROUP_LEVEL)){
            leafs = mutableSetOf()
        } else {
            leafs = null
        }
    }
    fun count(): Int = cnt.sum()
    fun getChildIndex(value: Long): Int {
        val idx: Long = (value shr level) and 1
        return idx.toInt()
    }

    fun insert(leaf: GeoLeaf<T>): GeoNode<T>? {
        leafs?.add(leaf)
        if(this.isEnd) {
            cnt[0]++
            return this
        }
        // Logic
        val idx = getChildIndex(leaf.geohash)
        cnt[idx]++
        if(children[idx] == null){
            children[idx] = GeoNode(this)
        }
        return children[idx]?.insert(leaf)
    }
    fun delete(leaf: GeoLeaf<T>) {
        leafs?.remove(leaf)
        if(this.isEnd){
            cnt[0]--;
        } else {
            val idx = getChildIndex(leaf.geohash)
            cnt[idx]--
        }
        parent?.delete(leaf)
    }
}