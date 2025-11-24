package com.quest.evrounting.data.simulation

import java.util.UUID
import kotlin.random.Random

enum class CarStatus {
    DRIVING_TO_STATION,
    WAITING_IN_QUEUE,
    CHARGING,
    FINISHED,
    STRANDED            // Bị kẹt (ví dụ: không tìm được trạm)
}

data class EV(
    val id: String,
    val batteryCapacityKwh: Double = 60.0, // Dung lượng pin mặc định
    val currentBatteryLevel: Double,
    val targetBatteryLevel: Double = 80.0, // Mặc định sạc đến 80%
    var status: CarStatus = CarStatus.DRIVING_TO_STATION
) {
    init {
        require(currentBatteryLevel in 0.0..100.0) { "Mức pin ban đầu phải từ 0 đến 100" }
        require(targetBatteryLevel in 0.0..100.0) { "Mức pin mục tiêu phải từ 0 đến 100" }
    }


    fun calculateChargingDuration(chargePowerKw: Double, batteryCapacityKwh: Double, currentBatteryLevel: Double, targetBatteryLevel: Double): Long {
        val requiredEnergyKwh = batteryCapacityKwh * (targetBatteryLevel - currentBatteryLevel) / 100   // chia 100 để đúng đơn vị %
        if (requiredEnergyKwh <= 0) return 0L

        // Giả sử hiệu suất sạc là 90%
        val chargingEfficiency = 0.9
        val hoursNeeded = requiredEnergyKwh / (chargePowerKw * chargingEfficiency)

        return (hoursNeeded * 3600 * 1000).toLong()
    }

    companion object {
        fun createRandomCar(): EV {
            val randomID = UUID.randomUUID().toString().substring(0, 13)
            // Dung lượng pin ngẫu nhiên từ 40kWh (xe nhỏ) đến 100kWh (xe lớn)
            val randomCapacity = Random.nextDouble(40.0, 100.0)
            val randomCurrentLevel = Random.nextDouble(15.0, 40.0)

            return EV(
                id = "CAR-$randomID",
                currentBatteryLevel = randomCurrentLevel,
                batteryCapacityKwh = randomCapacity
            )
        }
    }
}
