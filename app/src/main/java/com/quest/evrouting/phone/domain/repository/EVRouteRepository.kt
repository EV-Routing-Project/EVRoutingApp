package com.quest.evrouting.phone.domain.repository

import com.quest.evrouting.phone.domain.model.Location
import com.quest.evrouting.phone.domain.model.Path

interface EVRouteRepository {
    suspend fun getRoute(
        current: Location,
        target: Location,
    ): Path?
}