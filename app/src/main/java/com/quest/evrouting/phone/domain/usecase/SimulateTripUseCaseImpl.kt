package com.quest.evrouting.phone.domain.usecase

import android.util.Log
import com.mapbox.geojson.Point
import com.quest.evrouting.phone.domain.model.EVCar
import com.quest.evrouting.phone.domain.model.Route
import com.quest.evrouting.phone.domain.model.Vehicle
import com.quest.evrouting.phone.domain.repository.ChargePointRepository
import com.quest.evrouting.phone.domain.repository.EVRouteRepository
import com.quest.evrouting.phone.ui.viewmodel.TripState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext // -> CÓ THỂ CẦN THÊM IMPORT NÀY
import kotlin.math.abs

class SimulateTripUseCaseImpl(
    private val evRouteRepository: EVRouteRepository,
    private val chargePointRepository: ChargePointRepository
) : SimulateTripUseCase {

    private val SIMULATION_TICK_MS = 1000L
    private val CHARGING_TICK_MS = 100L
    private val CHARGE_PER_TICK_KWH = 2.0

    override fun execute(
        initialRoute: Route,
        vehicle: Vehicle,
        destination: Point
    ): Flow<TripState> = flow {
        Log.d("SIMULATION_UC", "UseCase `execute` được gọi (Chế độ đơn giản).")

        var currentVehicle = vehicle
        var remainingGeometry = initialRoute.geometry.toMutableList()
        var currentLocation = remainingGeometry.firstOrNull() ?: return@flow
        val upcomingChargePointIds = initialRoute.chargePointsOnRoute.toMutableList()

        val initialState = TripState(currentVehicle, currentLocation, isActive = true, isCharging = false)
        emit(initialState)
        delay(SIMULATION_TICK_MS)

        // --- VÒNG LẶP MÔ PHỎNG CHÍNH ---
        // SỬA LỖI Ở ĐÂY:
        while (coroutineContext.isActive && remainingGeometry.size > 1) {

            val nextChargePointId = upcomingChargePointIds.firstOrNull()
            val nextChargePoint = nextChargePointId?.let { chargePointRepository.getChargePointById(it) }

            if (nextChargePoint != null && arePointsEqual(currentLocation, nextChargePoint.point)) {
                // ... (Logic sạc pin không thay đổi)
                // CŨNG NÊN SỬA Ở ĐÂY
                val targetPower = currentVehicle.totalPowerKwh * 0.9
                while (coroutineContext.isActive && currentVehicle.currentPowerKwh < targetPower) {
                    val newPower = (currentVehicle.currentPowerKwh + CHARGE_PER_TICK_KWH).coerceAtMost(currentVehicle.totalPowerKwh)
                    currentVehicle = (currentVehicle as EVCar).copy(currentPowerKwh = newPower)

                    val currentState = TripState(currentVehicle, currentLocation, isActive = true, isCharging = true)
                    emit(currentState)
                    delay(CHARGING_TICK_MS)
                }
                upcomingChargePointIds.removeAt(0)
            }

            // Di chuyển đến điểm tiếp theo trên lộ trình
            // Kiểm tra lại để chắc chắn không bị lỗi index
            if (remainingGeometry.size <= 1) break

            currentLocation = remainingGeometry[1]
            val newCurrentPower = ((currentVehicle as EVCar).currentPowerKwh - 1).coerceAtLeast(0.0)
            currentVehicle = currentVehicle.copy(currentPowerKwh = newCurrentPower)
            remainingGeometry.removeAt(0)

            val currentState = TripState(currentVehicle, currentLocation, isActive = true, isCharging = false)
            emit(currentState)
            delay(SIMULATION_TICK_MS)
        }

        val finalState = TripState(currentVehicle, currentLocation, isActive = false, isCharging = false)
        emit(finalState)
        Log.d("SIMULATION_UC", "Đã đến đích! Kết thúc Flow mô phỏng.")

    }.flowOn(Dispatchers.Default)
}

private fun arePointsEqual(p1: Point, p2: Point, tolerance: Double = 0.00001): Boolean {
    return abs(p1.latitude() - p2.latitude()) < tolerance &&
            abs(p1.longitude() - p2.longitude()) < tolerance
}
