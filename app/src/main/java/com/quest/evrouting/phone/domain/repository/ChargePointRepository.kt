package com.quest.evrouting.phone.domain.repository

import com.quest.evrouting.phone.domain.model.ChargePoint

interface ChargePointRepository {
    suspend fun getAllChargePoints(): List<ChargePoint>
    suspend fun getChargePointById(id: Int): ChargePoint?
}
