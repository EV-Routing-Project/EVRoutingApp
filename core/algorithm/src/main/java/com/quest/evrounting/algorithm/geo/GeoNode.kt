package com.quest.evrounting.algorithm.geo

class GeoNode(val parent: GeoNode? = null) {
    val level: Int
    val isEnd: Boolean
    val children: Array<GeoNode?>
    val cnt: Array<Int>
    val stations: MutableSet<GeoStation>?
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
            stations = mutableSetOf()
        } else {
            stations = null
        }
    }
    fun getChildIndex(value: Long): Int {
        val idx: Long = (value shr level) and 1
        return idx.toInt()
    }

    fun insert(station: GeoStation): GeoNode? {
        stations?.add(station)
        if(this.isEnd) {
            cnt[0]++
            return this
        }
        // Logic
        val idx = getChildIndex(station.geohash)
        cnt[idx]++
        if(children[idx] == null){
            children[idx] = GeoNode(this)
        }
        return children[idx]?.insert(station)
    }
    fun delete(station: GeoStation) {
        stations?.remove(station)
        if(this.isEnd){
            cnt[0]--;
        } else {
            val idx = getChildIndex(station.geohash)
            cnt[idx]--
        }
        parent?.delete(station)
    }
}