package com.quest.evrouting.phone.domain.usecase

import android.util.Log
import com.mapbox.geojson.Point
import com.quest.evrouting.phone.domain.model.EVCar
import com.quest.evrouting.phone.domain.model.Path
import com.quest.evrouting.phone.domain.model.Vehicle
import com.quest.evrouting.phone.domain.repository.EVRouteRepository
import com.quest.evrouting.phone.domain.repository.PoiRepository
import com.quest.evrouting.phone.ui.viewmodel.TripState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext
import kotlin.math.abs

class SimulateTripUseCaseImpl(
    private val evRouteRepository: EVRouteRepository,
    private val poiRepository: PoiRepository
) : SimulateTripUseCase {

    // --- Các hằng số cấu hình cho việc mô phỏng ---
    private val SIMULATION_TICK_MS = 1000L // Thời gian giữa mỗi bước di chuyển
    private val CHARGING_TICK_MS = 100L   // Tốc độ mô phỏng sạc pin
    private val CHARGE_PER_TICK_KWH = 2.0 // Lượng pin sạc được mỗi tick
    private val CONSUMPTION_PER_TICK_KWH = 1.0 // Lượng pin tiêu thụ mỗi bước di chuyển (giả định)
    private val RECHARGE_PERCENTAGE = 0.9 // Sạc pin đến 90%

    override fun execute(
        initialRoute: Path,
        vehicle: Vehicle,
        destination: Point
    ): Flow<TripState> = flow {
        Log.d("SIMULATION_UC", "Bắt đầu mô phỏng chuyến đi.")

        // Khởi tạo trạng thái ban đầu
        var currentVehicle = vehicle as? EVCar ?: return@flow
        var remainingPolyline = initialRoute.decodedPolyline.toMutableList()
        var currentLocation = remainingPolyline.firstOrNull() ?: return@flow

        // TODO: Logic lấy danh sách POI trên lộ trình sẽ được thêm ở đây trong tương lai
        // val upcomingPois = mutableListOf<Poi>()

        // Phát ra trạng thái đầu tiên
        emit(TripState(currentVehicle, currentLocation, isActive = true, isCharging = false))
        delay(SIMULATION_TICK_MS)

        // Vòng lặp mô phỏng chính
        while (coroutineContext.isActive && remainingPolyline.size > 1) {
            val nextPoint = remainingPolyline[1] // Lấy điểm kế tiếp để kiểm tra

            // TODO: Logic kiểm tra nếu điểm tiếp theo là một trạm sạc
            /*
            val poiAtNextPoint = upcomingPois.find { poi -> arePointsEqual(nextPoint, poi.location) }
            if (poiAtNextPoint != null) {
                // --- Logic sạc pin ---
                Log.d("SIMULATION_UC", "Đã đến trạm sạc: ${poiAtNextPoint.name}. Bắt đầu sạc.")
                currentLocation = nextPoint
                remainingPolyline.removeAt(0)

                val targetPower = currentVehicle.totalPowerKwh * RECHARGE_PERCENTAGE
                while (coroutineContext.isActive && currentVehicle.currentPowerKwh < targetPower) {
                    val newPower = (currentVehicle.currentPowerKwh + CHARGE_PER_TICK_KWH).coerceAtMost(currentVehicle.totalPowerKwh)
                    currentVehicle = currentVehicle.copy(currentPowerKwh = newPower)
                    emit(TripState(currentVehicle, currentLocation, isActive = true, isCharging = true))
                    delay(CHARGING_TICK_MS)
                }

                Log.d("SIMULATION_UC", "Sạc pin hoàn tất.")
                upcomingPois.remove(poiAtNextPoint)
            }
            */

            // --- Logic di chuyển thông thường ---
            currentLocation = nextPoint // Di chuyển đến điểm tiếp theo
            remainingPolyline.removeAt(0) // Xóa điểm đã đi qua

            // Trừ pin (giả định)
            val newPower = (currentVehicle.currentPowerKwh - CONSUMPTION_PER_TICK_KWH).coerceAtLeast(0.0)
            currentVehicle = currentVehicle.copy(currentPowerKwh = newPower)

            // Phát ra trạng thái di chuyển
            emit(TripState(currentVehicle, currentLocation, isActive = true, isCharging = false))
            delay(SIMULATION_TICK_MS)
        }

        // Kết thúc mô phỏng
        val finalState = TripState(currentVehicle, currentLocation, isActive = false, isCharging = false)
        emit(finalState)
        Log.d("SIMULATION_UC", "Đã đến đích! Kết thúc mô phỏng.")

    }.flowOn(Dispatchers.Default) // Chạy mô phỏng trên một luồng nền

    /**
     * So sánh hai điểm Point với một sai số cho phép.
     */
    private fun arePointsEqual(p1: Point, p2: Point, tolerance: Double = 0.00001): Boolean {
        return abs(p1.latitude() - p2.latitude()) < tolerance &&
                abs(p1.longitude() - p2.longitude()) < tolerance
    }
}
