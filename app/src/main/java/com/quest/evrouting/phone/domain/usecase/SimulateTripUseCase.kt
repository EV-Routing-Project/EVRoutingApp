package com.quest.evrouting.phone.domain.usecase

import com.mapbox.geojson.Point
import com.quest.evrouting.phone.domain.model.Route
import com.quest.evrouting.phone.domain.model.Vehicle
import com.quest.evrouting.phone.ui.viewmodel.TripState
import kotlinx.coroutines.flow.Flow

/**
 * Use case chịu trách nhiệm mô phỏng một chuyến đi.
 * Nó phát ra một dòng (Flow) các trạng thái chuyến đi (TripState) theo thời gian.
 */
interface SimulateTripUseCase {
    fun execute(
        initialRoute: Route,
        vehicle: Vehicle,
        destination: Point
    ): Flow<TripState>
}
