package com.quest.evrouting.phone.domain.model

/**
 * Lớp dữ liệu đại diện cho một chiếc ô tô điện cụ thể.
 * Lớp này triển khai (implements) interface Vehicle.
 *
 * @param totalPowerKwh Tổng dung lượng pin của xe (tính bằng kWh).
 * @param currentPowerKwh Mức pin hiện tại của xe (tính bằng kWh).
 */
data class EVCar(
    override val totalPowerKwh: Double,
    override val currentPowerKwh: Double,
    override val averageSpeedKmh: Double,
) : Vehicle
