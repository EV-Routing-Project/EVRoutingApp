package com.quest.evrouting.phone.domain.model

interface Vehicle {
    // Tổng dung lượng pin của xe (ví dụ: 80 kWh)
    val totalPowerKwh: Double

    // Mức năng lượng hiện tại của xe (ví dụ: 50 kWh)
    val currentPowerKwh: Double

    val averageSpeedKmh: Double
}
