package com.quest.evrounting.data.simulation

import com.quest.evrounting.data.local.repository.POIRepository
import com.quest.evrounting.data.local.repository.PortStatusRepository
import com.quest.evrounting.data.model.dynamic.PortStatusLog
import java.util.PriorityQueue
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.random.Random


object SimulationEngine {

    private val eventQueue = PriorityQueue<Event>()

    // Dùng Map để quản lý các phiên sạc đang hoạt động.
    // Key là sessionId, Value là đối tượng ChargingSession.
    private val activeSessions = mutableMapOf<String, ChargingSession>()

    // Key: Timestamp đã làm tròn (mốc 5 phút)
    // Value: Map<ConnectionID, PortStatusLog> chứa trạng thái cuối cùng của các cổng đã thay đổi tại mốc đó
    private val stateBuffer = mutableMapOf<Long, MutableMap<Int, PortStatusLog>>()

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
//            println("-----------------------------------------------------")
//            println("⚡ [Xử lý] Sự kiện ${currentEvent.type} tại T = ${currentEvent.timestamp}")

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

        // Lưu batch vào database
        flushStateBufferToDatabase()
        isRunning = false
        println("\n✅ Hàng đợi sự kiện trống. Mô phỏng đã hoàn tất.")
    }


    fun reset() {
        eventQueue.clear()
        activeSessions.clear()
        isRunning = false
        println("🔄 SimulationEngine đã được reset.")
    }


    // --- HÀM GHI VÀO BUFFER (thay thế cho việc ghi trực tiếp vào DB) ---
    private fun bufferNewState(statusLog: PortStatusLog) {
        // Làm tròn xuống mốc 5p
        val fiveMinutesInMillis = 5 * 60 * 1000L
        val roundedTimestamp = (statusLog.simulationTimestamp / fiveMinutesInMillis) * fiveMinutesInMillis

        // Lấy hoặc tạo Map con cho mốc timestamp này
        val statesAtTimestamp = stateBuffer.getOrPut(roundedTimestamp) { mutableMapOf() }

        // Cập nhật trạng thái cho connectionId cụ thể.
        // Bản ghi `statusLog` giờ đã được gắn timestamp đã làm tròn
        statesAtTimestamp[statusLog.connectionId] = statusLog.copy(simulationTimestamp = roundedTimestamp)
    }


    // --- HÀM LƯU BUFFER VÀO DATABASE ---
    private suspend fun flushStateBufferToDatabase() {
        if (stateBuffer.isEmpty()) {
//            println("    -> Bộ đệm trống, không có gì để ghi.")
            return
        }

        // Lấy toàn bộ danh sách connection MỘT LẦN DUY NHẤT để tra cứu
        val allConnectionsMap = POIRepository.getAllConnections().associateBy { it.id }

        if (allConnectionsMap.isEmpty()) {
            println("    ❌ Lỗi: Không thể lấy danh sách connection gốc từ DB.")
            return
        }

        // Sắp xếp các mốc timestamp để ghi theo thứ tự thời gian
        val sortedTimestamps = stateBuffer.keys.sorted()

        // Trạng thái đã biết gần nhất, được mang theo qua các mốc thời gian
        val latestKnownStates = mutableMapOf<Int, PortStatusLog>()

        for (timestamp in sortedTimestamps) {
            val changesAtThisTimestamp = stateBuffer[timestamp] ?: continue

            // Cập nhật trạng thái đã biết với những thay đổi tại mốc này
            latestKnownStates.putAll(changesAtThisTimestamp)

            // Tạo snapshot hoàn chỉnh tại mốc `timestamp`:
            // Bao gồm những cổng đã thay đổi và cả những cổng không thay đổi
            val fullSnapshot = allConnectionsMap.keys.map { connId ->
                latestKnownStates[connId] ?: PortStatusLog(
                    connectionId = connId,
                    availablePorts = allConnectionsMap[connId]?.quantity ?: 0, // Trạng thái ban đầu
                    simulationTimestamp = timestamp
                )
            }

            // Gọi hàm ghi hàng loạt của Repository
            PortStatusRepository.insertNewStateForAll(fullSnapshot)
        }

        println("✅ Đã ghi thành công dữ liệu cho ${sortedTimestamps.size} mốc thời gian.")
    }


    // --- Các hàm xử lý nghiệp vụ cho từng loại sự kiện ---
    private suspend fun handleCarArrival(data: CarArrivalData, eventTimestamp: Long) {
        val latestStatus = PortStatusRepository.getLatestStatus(data.connectionId, eventTimestamp)
        if (latestStatus == null) {
            println("    ❌ Lỗi: Không tìm thấy trạng thái cho cổng sạc #${data.connectionId}.")
            return
        }
//        println("    -> Xe '${data.car.id}' đến cổng #${data.connectionId}. Trạng thái hiện tại: ${latestStatus.availablePorts} cổng trống.")

        if (latestStatus.availablePorts > 0) {
            // CÓ CHỖ TRỐNG
            latestStatus.availablePorts --
            latestStatus.simulationTimestamp = eventTimestamp
//            println("    -> ✅ Thành công: Xe '${data.car.id}' bắt đầu sạc. Số cổng trống còn lại: ${latestStatus.availablePorts}.")
            // Ghi nhận trạng thái mới
//            PortStatusRepository.insertNewState(latestStatus)
            bufferNewState(latestStatus)


            val mockCar = data.car
            val connection = POIRepository.getConnectionById(data.connectionId)
            if (connection == null){
                println("    ❌ Lỗi: Không tìm thấy cổng sạc #${data.connectionId}.")
                return
            }
            val connectionPowerKw = connection.powerKw ?: 0.0
            // Tính toán thời gian sạc (theo ms) thực tế dựa trên thông tin xe và trạm
            val chargingDuration = mockCar.calculateChargingDuration(connectionPowerKw, mockCar.batteryCapacityKwh, mockCar.currentBatteryLevel, mockCar.targetBatteryLevel)
            var finishTime = eventTimestamp + chargingDuration
//            println("    -> ⏳ Ước tính thời gian sạc cho xe '${mockCar.id}': ${chargingDuration / 1000} giây mô phỏng.")

            if (data.timeInterval == Utility.TimeInterval.PRE_SLEEP_PEAK || data.timeInterval == Utility.TimeInterval.DEEP_SLEEP) {
                // Tính thời điểm 0h sáng của ngày hôm sau bằng cách
                // Lấy mốc 0h của ngày hôm đó, rồi cộng thêm 1 ngày
                val startOfToday = eventTimestamp - (eventTimestamp % (1000 * 60 * 60 * 24))
                val startOfNextDay = startOfToday + (1000 * 60 * 60 * 24)

                // Thời điểm người dùng có thể lấy xe là ngẫu nhiên từ 5h đến 7h sáng hôm sau
                val pickupTime = startOfNextDay + Utility.getRandomDuration(
                    TimeUnit.HOURS.toMillis(5),
                    TimeUnit.HOURS.toMillis(7)
                )

                // Thời gian kết thúc thực tế sẽ là thời điểm nào đến sau: sạc đầy hoặc người dùng đến lấy xe
                finishTime = maxOf(finishTime, pickupTime)
//                println("    -> 🌙 Xe đến vào ban đêm. Thời gian kết thúc được điều chỉnh theo giờ lấy xe buổi sáng (khoảng 5-7h).")
            }

            val newSession = ChargingSession(
                sessionId = UUID.randomUUID().toString(), // Tạo ID duy nhất cho phiên
                carId = data.car.id,
                connectionId = data.connectionId,
                startTime = eventTimestamp,
                estimatedEndTime = finishTime
            )

            activeSessions[newSession.sessionId] = newSession
//            println("    -> 📝 Đã tạo phiên sạc mới: ${newSession.sessionId}")

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
//            println("    -> ⚠️ Thất bại: Tất cả các cổng tại #${data.connectionId} đều bận hoặc đang bảo trì. Xe '${data.car.id}' phải chờ.")
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
//        println("    -> Xe '${session.carId}' đã sạc xong tại cổng #${session.connectionId}.")

        latestStatus.availablePorts ++
        latestStatus.simulationTimestamp = eventTimestamp
//        println("    -> ✅ Cổng được giải phóng. Số cổng trống hiện tại: ${latestStatus.availablePorts}.")
//        PortStatusRepository.insertNewState(latestStatus)
        bufferNewState(latestStatus)


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
                        // Chỉ có thể bảo trì các cổng đang trống
                        portsToDisable = Random.nextInt(1, maxDisableable + 1)
//                        println("        -> 🔧 Connection #${connId}: Bảo trì $portsToDisable cổng. Số cổng khả dụng: ${latestStatus.availablePorts} -> ${latestStatus.availablePorts - portsToDisable}.")
                        latestStatus.availablePorts -= portsToDisable
                    } else {
//                        println("        -> ℹ️ Connection #${connId}: Không có cổng trống để bảo trì.")
                    }
                }
                MaintenanceScope.CONNECTION, MaintenanceScope.FULL_CHARGE_POINT -> {
                    // Vô hiệu hóa tất cả các cổng đang trống
                    portsToDisable = latestStatus.availablePorts
//                    println("        -> 🔧 Connection #${connId}: Bảo trì toàn bộ (${portsToDisable} cổng). Số cổng khả dụng: ${latestStatus.availablePorts} -> 0.")
                    latestStatus.availablePorts = 0
                }
            }

            if (portsToDisable > 0) {
                latestStatus.simulationTimestamp = eventTimestamp
//                PortStatusRepository.insertNewState(latestStatus)
                bufferNewState(latestStatus)


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
//            println("    -> ℹ️ Không có cổng nào được ghi nhận để khôi phục cho Connection #${connId}. Bỏ qua.")
            return
        }
//        println("    -> ✅ Bắt đầu khôi phục $portsToRestore cổng cho Connection #${connId}.")
        val latestStatus = PortStatusRepository.getLatestStatus(connId, eventTimestamp)
        val connectionInfo = POIRepository.getConnectionById(connId)

        if (latestStatus == null || connectionInfo == null) {
            println("        ❌ Lỗi: Không tìm thấy thông tin gốc hoặc trạng thái cho Connection #${connId} để khôi phục.")
            return
        }

        val maxQuantity = connectionInfo.quantity ?: 0
        val newAvailablePorts = (latestStatus.availablePorts + portsToRestore).coerceAtMost(maxQuantity)
//        println("        -> ✨ Connection #${connId}: Khôi phục hoàn tất. Số cổng khả dụng: ${latestStatus.availablePorts} -> ${newAvailablePorts}.")

        latestStatus.availablePorts = newAvailablePorts
        latestStatus.simulationTimestamp = eventTimestamp
//        PortStatusRepository.insertNewState(latestStatus)
        bufferNewState(latestStatus)
    }
}



//object SimulationEngine {
//
//    private val eventQueue = PriorityQueue<Event>()
//
//    // Dùng Map để quản lý các phiên sạc đang hoạt động.
//    // Key là sessionId, Value là đối tượng ChargingSession.
//    private val activeSessions = mutableMapOf<String, ChargingSession>()
//
//    @Volatile
//    private var isRunning = false
//
//    fun scheduleEvent(event: Event) {
//        eventQueue.add(event)
//    }
//
//
//    suspend fun run() {
//        if (isRunning) {
//            println("⚠️ Mô phỏng đã đang chạy.")
//            return
//        }
//        isRunning = true
//
//        Clock.start()
//        println("\n▶️ Bắt đầu vòng lặp mô phỏng...\n")
//
//        while (eventQueue.isNotEmpty()) {
//            val currentEvent = eventQueue.poll() ?: continue
//
//            // ------------ XỬ LÝ SỰ KIỆN ------------
//            println("-----------------------------------------------------")
//            println("⚡ [Xử lý] Sự kiện ${currentEvent.type} tại T = ${currentEvent.timestamp}")
//
//            // Dùng `when` với `sealed class` để đảm bảo xử lý hết các trường hợp
//            // When tương tự nhưng nâng cấp hơn switch-case
//            when (currentEvent.type) {
//                EventType.CAR_ARRIVAL -> {
//                    val data = currentEvent.data as? CarArrivalData
//                    if (data != null) {
//                        handleCarArrival(data, currentEvent.timestamp)
//                    }
//                }
//
//                EventType.CHARGING_FINISHED -> {
//                    val data = currentEvent.data as? ChargingFinishedData
//                    if (data != null) {
//                        handleChargingFinished(data, currentEvent.timestamp)
//                    }
//                }
//
//                EventType.MAINTENANCE_EVENT -> {
//                    val data = currentEvent.data as? MaintenanceEventData
//                    if (data != null) {
//                        handleMaintenanceEvent(data, currentEvent.timestamp)
//                    }
//                }
//
//                EventType.MAINTENANCE_RESTORED -> {
//                    val data = currentEvent.data as? MaintenanceEventData
//                    if (data != null) {
//                        handleMaintenanceRestored(data, currentEvent.timestamp)
//                    }
//                }
//
//                EventType.SIMULATION_END -> {
//                    println("🛑 Gặp sự kiện SIMULATION_END. Dừng mô phỏng.")
//                    eventQueue.clear() // Xóa hết các sự kiện còn lại
//                    continue
//                }
//                else -> println("-> Bỏ qua sự kiện chưa được xử lý: ${currentEvent.type}")
//            }
//        }
//
//        isRunning = false
//        println("\n✅ Hàng đợi sự kiện trống. Mô phỏng đã hoàn tất.")
//    }
//
//
//    fun reset() {
//        eventQueue.clear()
//        activeSessions.clear()
//        isRunning = false
//        println("🔄 SimulationEngine đã được reset.")
//    }
//
//    // --- Các hàm xử lý nghiệp vụ cho từng loại sự kiện ---
//    private suspend fun handleCarArrival(data: CarArrivalData, eventTimestamp: Long) {
//        val latestStatus = PortStatusRepository.getLatestStatus(data.connectionId, eventTimestamp)
//        if (latestStatus == null) {
//            println("    ❌ Lỗi: Không tìm thấy trạng thái cho cổng sạc #${data.connectionId}.")
//            return
//        }
//        println("    -> Xe '${data.car.id}' đến cổng #${data.connectionId}. Trạng thái hiện tại: ${latestStatus.availablePorts} cổng trống.")
//
//        if (latestStatus.availablePorts > 0) {
//            // CÓ CHỖ TRỐNG
//            latestStatus.availablePorts --
//            latestStatus.simulationTimestamp = eventTimestamp
//            println("    -> ✅ Thành công: Xe '${data.car.id}' bắt đầu sạc. Số cổng trống còn lại: ${latestStatus.availablePorts}.")
//            // Ghi nhận trạng thái mới
//            PortStatusRepository.insertNewState(latestStatus)
//
//            val mockCar = data.car
//            val connection = POIRepository.getConnectionById(data.connectionId)
//            if (connection == null){
//                println("    ❌ Lỗi: Không tìm thấy cổng sạc #${data.connectionId}.")
//                return
//            }
//            val connectionPowerKw = connection.powerKw ?: 0.0
//            // Tính toán thời gian sạc (theo ms) thực tế dựa trên thông tin xe và trạm
//            val chargingDuration = mockCar.calculateChargingDuration(connectionPowerKw, mockCar.batteryCapacityKwh, mockCar.currentBatteryLevel, mockCar.targetBatteryLevel)
//            var finishTime = eventTimestamp + chargingDuration
//            println("    -> ⏳ Ước tính thời gian sạc cho xe '${mockCar.id}': ${chargingDuration / 1000} giây mô phỏng.")
//
//            if (data.timeInterval == Utility.TimeInterval.PRE_SLEEP_PEAK || data.timeInterval == Utility.TimeInterval.DEEP_SLEEP) {
//                // Tính thời điểm 0h sáng của ngày hôm sau bằng cách
//                // Lấy mốc 0h của ngày hôm đó, rồi cộng thêm 1 ngày
//                val startOfToday = eventTimestamp - (eventTimestamp % (1000 * 60 * 60 * 24))
//                val startOfNextDay = startOfToday + (1000 * 60 * 60 * 24)
//
//                // Thời điểm người dùng có thể lấy xe là ngẫu nhiên từ 5h đến 7h sáng hôm sau
//                val pickupTime = startOfNextDay + Utility.getRandomDuration(
//                    TimeUnit.HOURS.toMillis(5),
//                    TimeUnit.HOURS.toMillis(7)
//                )
//
//                // Thời gian kết thúc thực tế sẽ là thời điểm nào đến sau: sạc đầy hoặc người dùng đến lấy xe
//                finishTime = maxOf(finishTime, pickupTime)
//                println("    -> 🌙 Xe đến vào ban đêm. Thời gian kết thúc được điều chỉnh theo giờ lấy xe buổi sáng (khoảng 5-7h).")
//            }
//
//            val newSession = ChargingSession(
//                sessionId = UUID.randomUUID().toString(), // Tạo ID duy nhất cho phiên
//                carId = data.car.id,
//                connectionId = data.connectionId,
//                startTime = eventTimestamp,
//                estimatedEndTime = finishTime
//            )
//
//            activeSessions[newSession.sessionId] = newSession
//            println("    -> 📝 Đã tạo phiên sạc mới: ${newSession.sessionId}")
//
//            // Lên lịch cho sự kiện sạc xong với thời gian đã tính toán
//            scheduleEvent(
//                Event(
//                    timestamp = finishTime,
//                    type = EventType.CHARGING_FINISHED,
//                    data = ChargingFinishedData(sessionId = newSession.sessionId)
//                )
//            )
//
//        } else {
//            // HẾT CHỖ
//            println("    -> ⚠️ Thất bại: Tất cả các cổng tại #${data.connectionId} đều bận hoặc đang bảo trì. Xe '${data.car.id}' phải chờ.")
//            // Trong tương lai, logic xử lý hàng chờ sẽ được thêm vào đây.
//        }
//    }
//
//    private suspend fun handleChargingFinished(data: ChargingFinishedData, eventTimestamp: Long) {
//        val session = activeSessions.remove(data.sessionId)
//        if (session == null) {
//            println("    ❌ Lỗi: Không tìm thấy phiên sạc đang hoạt động với ID ${data.sessionId}.")
//            return
//        }
//
//        val latestStatus = PortStatusRepository.getLatestStatus(session.connectionId, eventTimestamp)
//        if (latestStatus == null) {
//            println("    ❌ Lỗi: Không tìm thấy trạng thái cho cổng sạc #${session.connectionId}.")
//            return
//        }
//        println("    -> Xe '${session.carId}' đã sạc xong tại cổng #${session.connectionId}.")
//
//        latestStatus.availablePorts ++
//        latestStatus.simulationTimestamp = eventTimestamp
//        println("    -> ✅ Cổng được giải phóng. Số cổng trống hiện tại: ${latestStatus.availablePorts}.")
//        PortStatusRepository.insertNewState(latestStatus)
//
//        // Trong tương lai, có thể kiểm tra hàng chờ để cho xe tiếp theo vào sạc.
//    }
//
//    private suspend fun handleMaintenanceEvent(data: MaintenanceEventData, eventTimestamp: Long) {
//        // Chỉ truyền Connection ID, không có ChargePoint ID
//        val affectedConnections = mutableListOf<Int>()
//        when (data.scope){
//            MaintenanceScope.PORT, MaintenanceScope.CONNECTION -> {
//                data.connectionId?.let { affectedConnections.add(it) }
//            }
//            MaintenanceScope.FULL_CHARGE_POINT -> {
//                data.chargePointId?.let { chargePointId ->
//                    val connectionsInChargePoint =
//                        POIRepository.getConnectionsForChargePoint(chargePointId)
//                    affectedConnections.addAll(connectionsInChargePoint.map { it.id })
//                }
//            }
//        }
//
//        if (affectedConnections.isEmpty()) {
//            println("    -> ⚠️ Cảnh báo: Không tìm thấy cổng nào bị ảnh hưởng bởi sự kiện bảo trì. Bỏ qua.")
//            return
//        }
//
//        println("    -> Bắt đầu sự kiện bảo trì ${data.scope} cho ${affectedConnections.size} cổng.")
//        for (connId in affectedConnections) {
//            val latestStatus = PortStatusRepository.getLatestStatus(connId, eventTimestamp)
//            if (latestStatus == null) {
//                println("    ❌ Lỗi: Không tìm thấy trạng thái cho cổng #${connId}.")
//                continue
//            }
//
//            var portsToDisable = 0
//            when (data.scope) {
//                MaintenanceScope.PORT -> {
//                    val maxDisableable = latestStatus.availablePorts
//                    if (maxDisableable > 0) {
//                        // Chỉ có thể vô hiệu hóa các cổng đang trống
//                        portsToDisable = Random.nextInt(1, maxDisableable + 1)
//                        println("        -> 🔧 Connection #${connId}: Bảo trì ${portsToDisable} cổng. Số cổng khả dụng: ${latestStatus.availablePorts} -> ${latestStatus.availablePorts - portsToDisable}.")
//                        latestStatus.availablePorts -= portsToDisable
//                    } else {
//                        println("        -> ℹ️ Connection #${connId}: Không có cổng trống để bảo trì.")
//                    }
//                }
//                MaintenanceScope.CONNECTION, MaintenanceScope.FULL_CHARGE_POINT -> {
//                    // Vô hiệu hóa tất cả các cổng đang trống
//                    portsToDisable = latestStatus.availablePorts
//                    println("        -> 🔧 Connection #${connId}: Bảo trì toàn bộ (${portsToDisable} cổng). Số cổng khả dụng: ${latestStatus.availablePorts} -> 0.")
//                    latestStatus.availablePorts = 0
//                }
//            }
//
//            if (portsToDisable > 0) {
//                latestStatus.simulationTimestamp = eventTimestamp
//                PortStatusRepository.insertNewState(latestStatus)
//
//                // Lên lịch sự kiện khôi phục và TRUYỀN số cổng đã bảo trì
//                val restoreTime = eventTimestamp + data.durationMillis
//                scheduleEvent(
//                    Event(
//                        timestamp = restoreTime,
//                        type = EventType.MAINTENANCE_RESTORED,
//                        data = MaintenanceEventData(
//                            scope = data.scope,
//                            durationMillis = 0,
//                            connectionId = connId,
//                            portsAffected = portsToDisable
//                        )
//                    )
//                )
//            }
//        }
//    }
//
//    private suspend fun handleMaintenanceRestored(data: MaintenanceEventData, eventTimestamp: Long) {
//        val connId = data.connectionId
//        val portsToRestore = data.portsAffected
//        if (connId == null) {
//            println("    -> ❌ Lỗi: Không có ID Connection nào được cung cấp trong sự kiện khôi phục. Bỏ qua.")
//            return
//        }
//        if (portsToRestore <= 0) {
//            println("    -> ℹ️ Không có cổng nào được ghi nhận để khôi phục cho Connection #${connId}. Bỏ qua.")
//            return
//        }
//        println("    -> ✅ Bắt đầu khôi phục $portsToRestore cổng cho Connection #${connId}.")
//        val latestStatus = PortStatusRepository.getLatestStatus(connId, eventTimestamp)
//        val connectionInfo = POIRepository.getConnectionById(connId)
//
//        if (latestStatus == null || connectionInfo == null) {
//            println("        ❌ Lỗi: Không tìm thấy thông tin gốc hoặc trạng thái cho Connection #${connId} để khôi phục.")
//            return
//        }
//
//        val maxQuantity = connectionInfo.quantity ?: 0
//        val newAvailablePorts = (latestStatus.availablePorts + portsToRestore).coerceAtMost(maxQuantity)
//        println("        -> ✨ Connection #${connId}: Khôi phục hoàn tất. Số cổng khả dụng: ${latestStatus.availablePorts} -> ${newAvailablePorts}.")
//
//        latestStatus.availablePorts = newAvailablePorts
//        latestStatus.simulationTimestamp = eventTimestamp
//        PortStatusRepository.insertNewState(latestStatus)
//    }
//}
