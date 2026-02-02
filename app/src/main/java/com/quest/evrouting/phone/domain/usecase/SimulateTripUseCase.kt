package com.quest.evrouting.phone.domain.usecase

import com.mapbox.geojson.Point
import com.quest.evrouting.phone.domain.model.Path
import com.quest.evrouting.phone.domain.model.Vehicle
import com.quest.evrouting.phone.ui.viewmodel.TripState
import kotlinx.coroutines.flow.Flow

interface SimulateTripUseCase {
    fun execute(
        initialRoute: Path,
        vehicle: Vehicle,
        destination: Point
    ): Flow<TripState>
}
