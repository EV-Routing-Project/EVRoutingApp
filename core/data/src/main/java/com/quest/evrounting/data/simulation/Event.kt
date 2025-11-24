package com.quest.evrounting.data.simulation

enum class EventType {
    CAR_ARRIVAL,
    CHARGING_STARTED,
    CHARGING_FINISHED,
    QUEUE_JOINED,
    CONNECTION_MAINTENANCE,
    CONNECTION_RESTORED,
    SIMULATION_END,
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

data class ConnectionMaintenanceData(
    val connectionId: Int
) : EventData()

data class ConnectionRestoredData(
    val connectionId: Int
) : EventData()

data class Event(
    val timestamp: Long,
    val type: EventType,
    val data: EventData? = null // Có thể null cho các sự kiện không cần dữ liệu như SIMULATION_END
) : Comparable<Event> {

    // override lại hàm compareTo mặc định của PriorityQueue
    override fun compareTo(other: Event): Int = this.timestamp.compareTo(other.timestamp)
}
