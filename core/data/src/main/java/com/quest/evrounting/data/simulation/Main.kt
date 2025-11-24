package com.quest.evrounting.data.simulation

import com.quest.evrounting.data.local.database.DatabaseFactory
import com.quest.evrounting.data.local.repository.POIRepository
import com.quest.evrounting.data.local.repository.PortStatusRepository
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import kotlin.random.Random

fun main() = runBlocking {
    println("--- CHƯƠNG TRÌNH MÔ PHỎNG SẠC XE ĐIỆN ---")
    println("\nĐang kết nối tới cơ sở dữ liệu...")
    DatabaseFactory.connect()


    println("\nĐang dọn dẹp log mô phỏng cũ và reset Engine...")
    SimulationEngine.reset()
    PortStatusRepository.clearSimulationLogs()
    println("✅ Dọn dẹp hoàn tất.")


    println("\nĐang lên lịch cho các xe đến trong một khoảng thời gian mô phỏng...")
    val availableConnectionIds = POIRepository.getAllConnetionIDs()
    if (availableConnectionIds.isEmpty()) {
        println("❌ LỖI: Không tìm thấy bất kỳ Connection nào trong cơ sở dữ liệu. Dừng mô phỏng.")
        return@runBlocking
    }
    println("✅ Tìm thấy ${availableConnectionIds.size} trạm sạc có thể mô phỏng.")

    // Giới hạn thời gian mô phỏng (ví dụ: 1 ngày)
    val simulationDurationMillis = TimeUnit.DAYS.toMillis(1)
    var nextArrivalTime = 0L
    while (nextArrivalTime < simulationDurationMillis) {
        // Xét khung giờ
        val timeInterval = Utility.getTimeInterval(nextArrivalTime)
        val timeToNextArrival = Random.nextLong(timeInterval.minMillis, timeInterval.maxMillis)
        // Thời điểm xe tiếp theo sẽ đến
        nextArrivalTime += timeToNextArrival
        if (nextArrivalTime > simulationDurationMillis) {
            break
        }

        val numberOfCarsInGroup = Utility.determineGroupSize(timeInterval)
        println("    -> 🌊 Tại T≈$nextArrivalTime (Khung giờ: $timeInterval) có $numberOfCarsInGroup xe đến sạc tại các trạm.")
        repeat(numberOfCarsInGroup){
            val littleDelay = Random.nextLong(0, 2000)      // Đảm bảo luôn nhỏ hơn timeToNextArrival
            // Các xe đến sẽ cách nhau từ 0ms-2s
            val finalTime = nextArrivalTime + littleDelay
            if (finalTime <= simulationDurationMillis) {
                val randomEV = EV.createRandomCar()
                val randomTargetConnectionId = availableConnectionIds.random()
                SimulationEngine.scheduleEvent(
                    Event(
                        timestamp = finalTime,
                        type = EventType.CAR_ARRIVAL,
                        data = CarArrivalData(car = randomEV, connectionId = randomTargetConnectionId)
                    )
                )
            }
        }
    }
    println("✅ Đã tạo xong các sự kiện xe đến trong vòng ${TimeUnit.MILLISECONDS.toDays(simulationDurationMillis)} ngày.")

    SimulationEngine.run()
}

