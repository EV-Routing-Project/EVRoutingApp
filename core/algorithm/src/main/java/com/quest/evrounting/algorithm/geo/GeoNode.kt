package com.quest.evrounting.algorithm.geo

class GeoNode (val parent: GeoNode? = null,
               val children: Array<GeoNode?> = arrayOfNulls(2)) {
    val level: Int
    val isEnd: Boolean
    init {
        if(parent != null){
            level = parent.level - 1
            if(level == 0){
                isEnd = true
            } else {
                isEnd = false
            }
        } else {
            level = GeoUtils.MAX_LEVEL
            isEnd = false
        }
    }
    fun getChildIndex(value: Long): Int{
        val idx: Long = (value shr level) and 1;
        return idx.toInt();
    }
    fun insert() {

    }
}