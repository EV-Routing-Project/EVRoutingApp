package com.quest.evrounting.data.simulation

import com.quest.evrounting.data.local.repository.POIRepository
import com.quest.evrounting.data.local.repository.PortStatusRepository
import java.util.PriorityQueue
import java.util.UUID


object SimulationEngine {

    private val eventQueue = PriorityQueue<Event>()

    // Dùng Map để quản lý các phiên sạc đang hoạt động.
    // Key là sessionId, Value là đối tượng ChargingSession.
    private val activeSessions = mutableMapOf<String, ChargingSession>()

    @Volatile
    private var isRunning = false

    fun scheduleEvent(event: Event) {
        eventQueue.add(event)
    }


    suspend fun run() {
        if (isRunning) {
            println("⚠️ Mô phỏng đã đang chạy.")
            return
        }
        isRunning = true

        Clock.start()
        println("\n▶️ Bắt đầu vòng lặp mô phỏng...\n")

        while (eventQueue.isNotEmpty()) {
            val currentEvent = eventQueue.poll() ?: continue

            // ------------ XỬ LÝ SỰ KIỆN ------------
            println("-----------------------------------------------------")
            println("⚡ [Xử lý] Sự kiện ${currentEvent.type} tại T = ${currentEvent.timestamp}")

            // Dùng `when` với `sealed class` để đảm bảo xử lý hết các trường hợp
            // When tương tự nhưng nâng cấp hơn switch-case
            when (currentEvent.type) {
                EventType.CAR_ARRIVAL -> {
                    val data = currentEvent.data as? CarArrivalData
                    if (data != null) {
                        handleCarArrival(data, currentEvent.timestamp)
                    }
                }

                EventType.CHARGING_FINISHED -> {
                    val data = currentEvent.data as? ChargingFinishedData
                    if (data != null) {
                        handleChargingFinished(data, currentEvent.timestamp)
                    }
                }

//                EventType.CONNECTION_MAINTENANCE -> {
//                    val data = currentEvent.data as? ConnectionDownData ?: return
//                    // Ghi nhận trạng thái mới: Số cổng khả dụng = 0
//                    PortStatusRepository.recordStateChange(
//                        PortStateChangeRequest(
//                            connectionId = data.connectionId,
//                            newAvailablePorts = 0, // BẢO TRÌ = 0 CỔNG
//                            timestamp = event.timestamp
//                        )
//                    )
//                    println("    -> Cảnh báo: Trạm sạc #${data.connectionId} đã ngưng hoạt động để bảo trì.")
//                }
//
//                EventType.CONNECTION_RESTORED -> {
//                    val data = currentEvent.data as? ConnectionRestoredData ?: return
//                    // Giả sử sau khi sửa xong, trạm có lại 2 cổng
//                    // (Bạn có thể lấy số cổng gốc từ DB nếu cần)
//                    val restoredPortCount = 2
//                    // Ghi nhận trạng thái mới: Số cổng khả dụng được khôi phục
//                    PortStatusRepository.recordStateChange(
//                        PortStateChangeRequest(
//                            connectionId = data.connectionId,
//                            newAvailablePorts = restoredPortCount,
//                            timestamp = event.timestamp
//                        )
//                    )
//                    println("    -> Thông báo: Trạm sạc #${data.connectionId} đã hoạt động trở lại với $restoredPortCount cổng.")
//                }

                EventType.SIMULATION_END -> {
                    println("🛑 Gặp sự kiện SIMULATION_END. Dừng mô phỏng.")
                    eventQueue.clear() // Xóa hết các sự kiện còn lại
                    continue
                }
                else -> println("-> Bỏ qua sự kiện chưa được xử lý: ${currentEvent.type}")
            }
        }

        isRunning = false
        println("\n✅ Hàng đợi sự kiện trống. Mô phỏng đã hoàn tất.")
    }


    fun reset() {
        eventQueue.clear()
        activeSessions.clear()
        isRunning = false
        println("🔄 SimulationEngine đã được reset.")
    }

    // --- Các hàm xử lý nghiệp vụ cho từng loại sự kiện ---
    private suspend fun handleCarArrival(data: CarArrivalData, eventTimestamp: Long) {
        val latestStatus = PortStatusRepository.getLatestStatus(data.connectionId, eventTimestamp)

        if (latestStatus == null) {
            println("    ❌ Lỗi: Không tìm thấy trạng thái cho cổng sạc #${data.connectionId}.")
            return
        }

        println("    -> Xe '${data.car.id}' đến cổng #${data.connectionId}. Trạng thái hiện tại: ${latestStatus.availablePorts} cổng trống.")

        if (latestStatus.availablePorts > 0) {
            // CÓ CHỖ TRỐNG
            latestStatus.availablePorts --
            latestStatus.simulationTimestamp = eventTimestamp
            println("    -> ✅ Thành công: Xe '${data.car.id}' bắt đầu sạc. Số cổng trống còn lại: ${latestStatus.availablePorts}.")
            // Ghi nhận trạng thái mới
            PortStatusRepository.insertNewState(latestStatus)


            val mockCar = data.car
            val connection = POIRepository.getConnectionById(data.connectionId)

            if (connection == null){
                println("    ❌ Lỗi: Không tìm thấy cổng sạc #${data.connectionId}.")
                return
            }
            val connectionPowerKw = connection.powerKw ?: 0.0

            // Tính toán thời gian sạc (theo ms) thực tế dựa trên thông tin xe và trạm
            val chargingDuration = mockCar.calculateChargingDuration(connectionPowerKw, mockCar.batteryCapacityKwh, mockCar.currentBatteryLevel, mockCar.targetBatteryLevel)
            val finishTime = eventTimestamp + chargingDuration

            println("    -> ⏳ Ước tính thời gian sạc cho xe '${mockCar.id}': ${chargingDuration / 1000} giây mô phỏng.")

            val newSession = ChargingSession(
                sessionId = UUID.randomUUID().toString(), // Tạo ID duy nhất cho phiên
                carId = data.car.id,
                connectionId = data.connectionId,
                startTime = eventTimestamp,
                estimatedEndTime = finishTime
            )

            activeSessions[newSession.sessionId] = newSession
            println("    -> 📝 Đã tạo phiên sạc mới: ${newSession.sessionId}")

            // Lên lịch cho sự kiện sạc xong với thời gian đã tính toán
            scheduleEvent(
                Event(
                    timestamp = finishTime,
                    type = EventType.CHARGING_FINISHED,
                    data = ChargingFinishedData(sessionId = newSession.sessionId)
                )
            )

        } else {
            // HẾT CHỖ
            println("    -> ⚠️ Thất bại: Tất cả các cổng tại #${data.connectionId} đều bận. Xe '${data.car.id}' phải chờ.")
            // Trong tương lai, logic xử lý hàng chờ sẽ được thêm vào đây.
        }
    }


    private suspend fun handleChargingFinished(data: ChargingFinishedData, eventTimestamp: Long) {
        val session = activeSessions.remove(data.sessionId)
        if (session == null) {
            println("    ❌ Lỗi: Không tìm thấy phiên sạc đang hoạt động với ID ${data.sessionId}.")
            return
        }

        val latestStatus = PortStatusRepository.getLatestStatus(session.connectionId, eventTimestamp)
        if (latestStatus == null) {
            println("    ❌ Lỗi: Không tìm thấy trạng thái cho cổng sạc #${session.connectionId}.")
            return
        }
        println("    -> Xe '${session.carId}' đã sạc xong tại cổng #${session.connectionId}.")

        latestStatus.availablePorts ++
        latestStatus.simulationTimestamp = eventTimestamp
        println("    -> ✅ Cổng được giải phóng. Số cổng trống hiện tại: ${latestStatus.availablePorts}.")

        PortStatusRepository.insertNewState(latestStatus)

        // Trong tương lai, có thể kiểm tra hàng chờ để cho xe tiếp theo vào sạc.
    }
}
