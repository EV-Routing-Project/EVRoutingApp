package com.quest.evrouting.data.simulation

import com.quest.evrouting.data.local.repository.POIRepository
import com.quest.evrouting.data.local.repository.PortStatusRepository
import java.util.PriorityQueue
import java.util.UUID
import kotlin.random.Random


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

                EventType.MAINTENANCE_EVENT -> {
                    val data = currentEvent.data as? MaintenanceEventData
                    if (data != null) {
                        handleMaintenanceEvent(data, currentEvent.timestamp)
                    }
                }

                EventType.MAINTENANCE_RESTORED -> {
                    val data = currentEvent.data as? MaintenanceEventData
                    if (data != null) {
                        handleMaintenanceRestored(data, currentEvent.timestamp)
                    }
                }

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
            println("    -> ⚠️ Thất bại: Tất cả các cổng tại #${data.connectionId} đều bận hoặc đang bảo trì. Xe '${data.car.id}' phải chờ.")
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

    private suspend fun handleMaintenanceEvent(data: MaintenanceEventData, eventTimestamp: Long) {
        // Chỉ truyền Connection ID, không có ChargePoint ID
        val affectedConnections = mutableListOf<Int>()
        when (data.scope){
            MaintenanceScope.PORT, MaintenanceScope.CONNECTION -> {
                data.connectionId?.let { affectedConnections.add(it) }
            }
            MaintenanceScope.FULL_CHARGE_POINT -> {
                data.chargePointId?.let { chargePointId ->
                    val connectionsInChargePoint =
                        POIRepository.getConnectionsForChargePoint(chargePointId)
                    affectedConnections.addAll(connectionsInChargePoint.map { it.id })
                }
            }
        }

        if (affectedConnections.isEmpty()) {
            println("    -> ⚠️ Cảnh báo: Không tìm thấy cổng nào bị ảnh hưởng bởi sự kiện bảo trì. Bỏ qua.")
            return
        }

        println("    -> Bắt đầu sự kiện bảo trì ${data.scope} cho ${affectedConnections.size} cổng.")
        for (connId in affectedConnections) {
            val latestStatus = PortStatusRepository.getLatestStatus(connId, eventTimestamp)
            if (latestStatus == null) {
                println("    ❌ Lỗi: Không tìm thấy trạng thái cho cổng #${connId}.")
                continue
            }

            var portsToDisable = 0
            when (data.scope) {
                MaintenanceScope.PORT -> {
                    val maxDisableable = latestStatus.availablePorts
                    if (maxDisableable > 0) {
                        // Chỉ có thể vô hiệu hóa các cổng đang trống
                        portsToDisable = Random.nextInt(1, maxDisableable + 1)
                        println("        -> 🔧 Connection #${connId}: Bảo trì ${portsToDisable} cổng. Số cổng khả dụng: ${latestStatus.availablePorts} -> ${latestStatus.availablePorts - portsToDisable}.")
                        latestStatus.availablePorts -= portsToDisable
                    } else {
                        println("        -> ℹ️ Connection #${connId}: Không có cổng trống để bảo trì.")
                    }
                }
                MaintenanceScope.CONNECTION, MaintenanceScope.FULL_CHARGE_POINT -> {
                    // Vô hiệu hóa tất cả các cổng đang trống
                    portsToDisable = latestStatus.availablePorts
                    println("        -> 🔧 Connection #${connId}: Bảo trì toàn bộ (${portsToDisable} cổng). Số cổng khả dụng: ${latestStatus.availablePorts} -> 0.")
                    latestStatus.availablePorts = 0
                }
            }

            if (portsToDisable > 0) {
                latestStatus.simulationTimestamp = eventTimestamp
                PortStatusRepository.insertNewState(latestStatus)

                // Lên lịch sự kiện khôi phục và TRUYỀN số cổng đã bảo trì
                val restoreTime = eventTimestamp + data.durationMillis
                scheduleEvent(
                    Event(
                        timestamp = restoreTime,
                        type = EventType.MAINTENANCE_RESTORED,
                        data = MaintenanceEventData(
                            scope = data.scope,
                            durationMillis = 0,
                            connectionId = connId,
                            portsAffected = portsToDisable
                        )
                    )
                )
            }
        }
    }

    private suspend fun handleMaintenanceRestored(data: MaintenanceEventData, eventTimestamp: Long) {
        val connId = data.connectionId
        val portsToRestore = data.portsAffected
        if (connId == null) {
            println("    -> ❌ Lỗi: Không có ID Connection nào được cung cấp trong sự kiện khôi phục. Bỏ qua.")
            return
        }
        if (portsToRestore <= 0) {
            println("    -> ℹ️ Không có cổng nào được ghi nhận để khôi phục cho Connection #${connId}. Bỏ qua.")
            return
        }
        println("    -> ✅ Bắt đầu khôi phục $portsToRestore cổng cho Connection #${connId}.")
        val latestStatus = PortStatusRepository.getLatestStatus(connId, eventTimestamp)
        val connectionInfo = POIRepository.getConnectionById(connId)

        if (latestStatus == null || connectionInfo == null) {
            println("        ❌ Lỗi: Không tìm thấy thông tin gốc hoặc trạng thái cho Connection #${connId} để khôi phục.")
            return
        }

        val maxQuantity = connectionInfo.quantity ?: 0
        val newAvailablePorts = (latestStatus.availablePorts + portsToRestore).coerceAtMost(maxQuantity)
        println("        -> ✨ Connection #${connId}: Khôi phục hoàn tất. Số cổng khả dụng: ${latestStatus.availablePorts} -> ${newAvailablePorts}.")

        latestStatus.availablePorts = newAvailablePorts
        latestStatus.simulationTimestamp = eventTimestamp
        PortStatusRepository.insertNewState(latestStatus)
    }
}
