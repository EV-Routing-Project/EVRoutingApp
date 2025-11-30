package com.quest.evrounting.data.simulation

import com.quest.evrounting.data.local.database.DatabaseFactory
import com.quest.evrounting.data.local.repository.POIRepository
import com.quest.evrounting.data.local.repository.PortStatusRepository
import com.quest.evrounting.data.simulation.mapper.toConnectionSim
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

suspend fun runSimulation (url: String, driver: String, user: String, password: String) {
    println("--- CHƯƠNG TRÌNH MÔ PHỎNG SẠC XE ĐIỆN ---")
    println("\nĐang kết nối tới cơ sở dữ liệu...")
    DatabaseFactory.connect(url, driver, user, password)


    println("\nĐang dọn dẹp log mô phỏng cũ và reset Engine...")
    SimulationEngine.reset()
    PortStatusRepository.clearSimulationLogs()
    println("✅ Dọn dẹp hoàn tất.")


    println("\nĐang tìm tất cả Connection để mô phỏng...")
    val totalPort = POIRepository.getTotalPortCount()
    val allChargePointIds = POIRepository.getAllChargePointIDs()
    val allConnections = POIRepository.getAllConnections()
    val allConnectionSim = allConnections.mapNotNull { it.toConnectionSim() }
    if (allConnectionSim.isEmpty()) {
        println("❌ LỖI: Không tìm thấy bất kỳ Connection nào trong cơ sở dữ liệu. Dừng mô phỏng.")
        return
    }
    println("✅ Tìm thấy ${allConnectionSim.size} Connection có thể mô phỏng.")


    // Giới hạn thời gian mô phỏng (ví dụ: 1 ngày)
    println("\nĐang lên lịch cho các sự kiện (xe đến và bảo trì trạm) trong khoảng thời gian mô phỏng...")
    val simulationDurationMillis = TimeUnit.DAYS.toMillis(1)
    var carArrivalCount = 0
    var maintenanceCount = 0
    var nextArrivalTime = 0L
    var nextMaintenanceTime = 0L

    while (nextArrivalTime < simulationDurationMillis || nextMaintenanceTime < simulationDurationMillis) {

    // ------------------ KHAI BÁO CHUNG ------------------
        val currentFormula = allFormulas.random()
        val currentStrategy = allStrategy.random()
        val (weightedList, totalWeight) = ConnectionSim.createWeightedList(allConnectionSim, currentFormula, currentStrategy)


    //  ------------------ SỰ KIỆN XE ĐẾN ------------------
        // Xét khung giờ
        val timeInterval = Utility.getTimeInterval(nextArrivalTime)
        val timeToNextArrival = Utility.getRandomDuration(timeInterval.minMillis, timeInterval.maxMillis)
        // Thời điểm xe tiếp theo sẽ đến
        nextArrivalTime += timeToNextArrival

        if (nextArrivalTime <= simulationDurationMillis) {
            val numberOfCarsInGroup = Utility.determineGroupSize(timeInterval, allConnectionSim.size)
            println("    -> 🌊 Tại T≈$nextArrivalTime (Khung giờ: $timeInterval) có $numberOfCarsInGroup xe đến sạc tại các trạm.")

            repeat(numberOfCarsInGroup){
                val littleDelay = Utility.getRandomDuration(0,2000)      // Đã đảm bảo luôn nhỏ hơn timeToNextArrival
                // Các xe đến sẽ cách nhau từ 0ms-<2s
                val finalTime = nextArrivalTime + littleDelay
                if (finalTime <= simulationDurationMillis) {
                    val randomEV = EV.createRandomCar()
                    val targetConnectionId = ConnectionSim.selectByWeight(weightedList, totalWeight)

                    SimulationEngine.scheduleEvent(
                        Event(
                            timestamp = finalTime,
                            type = EventType.CAR_ARRIVAL,
                            data = CarArrivalData(car = randomEV, connectionId = targetConnectionId)
                        )
                    )
                    carArrivalCount++
                }
            }
        }


    //  ------------------ SỰ KIỆN BẢO TRÌ ------------------
        // Thời điểm diễn ra đợt bảo trì tiếp theo (TB: 15 ngày)
        val timeToNextMaintenance = Utility.getRandomDuration(TimeUnit.DAYS.toMillis(12),TimeUnit.DAYS.toMillis(18))
        nextMaintenanceTime += timeToNextMaintenance
        if (nextMaintenanceTime <= simulationDurationMillis) {
            println("\n--- 🔧 Đợt bảo trì tiếp theo tại T≈${nextMaintenanceTime / 1000}s ---")

            // Xác định quy mô theo đợt
            val scopeRoll = (1..100).random()
            val selectedScope = when {
                scopeRoll <= 70 -> MaintenanceScope.PORT            // 70%
                scopeRoll <= 95 -> MaintenanceScope.CONNECTION      // 25%
                else -> MaintenanceScope.FULL_CHARGE_POINT          // 5%
            }
            println("    -> Phạm vi đợt này: $selectedScope")

            // Xác định số lượng (numberOfStationsToMaintain ở đây có thể là số trụ sạc hoặc số loại connection hoặc số trạm sạc)
            val numberOfStationsToMaintain = when (selectedScope) {
                MaintenanceScope.PORT -> {
                    // Bảo trì khoảng 30% - 50% tổng số cổng (trung bình 40%)
                    val minCount = (totalPort * 0.3).toInt().coerceAtLeast(1)
                    val maxCount = (totalPort * 0.5).toInt().coerceAtLeast(minCount)
                    (minCount..maxCount).random()
                }
                MaintenanceScope.CONNECTION -> {
                    // Bảo trì khoảng 20% - 40% tổng số loại connection (trung bình 30%)
                    val minCount = (allConnectionSim.size * 0.2).toInt().coerceAtLeast(1)
                    val maxCount = (allConnectionSim.size * 0.4).toInt().coerceAtLeast(minCount)
                    (minCount..maxCount).random()
                }
                MaintenanceScope.FULL_CHARGE_POINT -> {
                    // Bảo trì khoảng 2% - 8% tổng số trạm sạc (trung bình 5%)
                    val minCount = (allChargePointIds.size * 0.02).toInt().coerceAtLeast(1)
                    val maxCount = (allChargePointIds.size * 0.08).toInt().coerceAtLeast(minCount)
                    (minCount..maxCount).random()
                }
            }
            println("    -> Số lượng mục tiêu cần bảo trì: $numberOfStationsToMaintain")

            // Tạo sự kiện bảo trì
            repeat(numberOfStationsToMaintain) {
                // 80% tỉ lệ được bảo trì
                if (Utility.shouldEventOccur(80)) {
                    val littleDelay = Utility.getRandomDuration(0, TimeUnit.MINUTES.toMillis(15))
                    val finalTime = nextMaintenanceTime + littleDelay
                    if (finalTime <= simulationDurationMillis) {
                        val maintenanceDurationMillis = when (selectedScope) {
                            MaintenanceScope.PORT -> Utility.getRandomDuration(TimeUnit.MINUTES.toMillis(30), TimeUnit.HOURS.toMillis(2))            // Nhanh: 30p - 2h
                            MaintenanceScope.CONNECTION -> Utility.getRandomDuration(TimeUnit.MINUTES.toMillis(90), TimeUnit.HOURS.toMillis(5))      // Vừa: 1h30p - 5h
                            MaintenanceScope.FULL_CHARGE_POINT -> Utility.getRandomDuration(TimeUnit.HOURS.toMillis(4), TimeUnit.HOURS.toMillis(12)) // Lâu: 4h - 12h
                        }

                        val eventData: MaintenanceEventData? = when (selectedScope) {
                            MaintenanceScope.PORT, MaintenanceScope.CONNECTION -> {
                                allConnectionSim.randomOrNull()?.let { randomConnection ->
                                    MaintenanceEventData(
                                        scope = selectedScope,
                                        durationMillis = maintenanceDurationMillis,
                                        connectionId = randomConnection.id
                                    )
                                }
                            }
                            MaintenanceScope.FULL_CHARGE_POINT -> {
                                allChargePointIds.randomOrNull()?.let { randomChargePointId ->
                                    MaintenanceEventData(
                                        scope = selectedScope,
                                        durationMillis = maintenanceDurationMillis,
                                        chargePointId = randomChargePointId
                                    )
                                }
                            }
                        }

                        if (eventData != null) {
                            SimulationEngine.scheduleEvent(
                                Event(
                                    timestamp = finalTime,
                                    type = EventType.MAINTENANCE_EVENT,
                                    data = eventData
                                )
                            )
                            maintenanceCount++
                        }
                    }
                }
            }
        }
    }
    println("✅ Đã tạo xong các sự kiện xe đến trong vòng ${TimeUnit.MILLISECONDS.toDays(simulationDurationMillis)} ngày.")

    SimulationEngine.run()
    println("Có tổng cộng $carArrivalCount sự kiện xe đến sạc trong ${TimeUnit.MILLISECONDS.toDays(simulationDurationMillis)} ngày.")
    println("Có tổng cộng $maintenanceCount sự kiện bảo trì trong ${TimeUnit.MILLISECONDS.toDays(simulationDurationMillis)} ngày.")
    println("\n--- KẾT THÚC MÔ PHỎNG ---")
}

fun main() = runBlocking {
    runSimulation(
        url = DatabaseFactory.URL,
        driver = DatabaseFactory.DRIVER,
        user = DatabaseFactory.USER,
        password = DatabaseFactory.PASSWORD
    )
}