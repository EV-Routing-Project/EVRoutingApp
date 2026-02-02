package com.quest.evrouting.phone.domain.repository

import com.quest.evrouting.phone.domain.model.POI

interface PoiRepository {
    suspend fun getAllPois(): List<POI>
    suspend fun getPoiById(id: String): POI?
}
