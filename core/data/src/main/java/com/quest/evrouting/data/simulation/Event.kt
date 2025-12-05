package com.quest.evrouting.data.simulation

enum class EventType {
    CAR_ARRIVAL,
    CHARGING_STARTED,
    CHARGING_FINISHED,
    QUEUE_JOINED,
    MAINTENANCE_EVENT,
    MAINTENANCE_RESTORED,
    SIMULATION_END,
}

enum class MaintenanceScope {
    PORT,
    CONNECTION,
    FULL_CHARGE_POINT
}

sealed class EventData

data class CarArrivalData(
    val car: EV,
    val connectionId: Int
) : EventData()

//data class ChargingStartedData(
//    val carId: String,
//    val connectionId: Int
//) : EventData()
//
//data class QueueJoinedData(
//    val carId: String,
//    val connectionId: Int
//) : EventData()


data class ChargingFinishedData(
    val sessionId: String,
) : EventData()

data class MaintenanceEventData(
    // Chỉ cần chargePointId hoặc connectionId, không cùng lúc
    val chargePointId: Int? = null,
    val connectionId: Int? = null,
    val scope: MaintenanceScope,
    val durationMillis: Long,
    val portsAffected: Int = 0
) : EventData()

data class Event(
    val timestamp: Long,
    val type: EventType,
    val data: EventData? = null // Có thể null cho các sự kiện không cần dữ liệu như SIMULATION_END
) : Comparable<Event> {

    // override lại hàm compareTo mặc định của PriorityQueue
    override fun compareTo(other: Event): Int = this.timestamp.compareTo(other.timestamp)
}
