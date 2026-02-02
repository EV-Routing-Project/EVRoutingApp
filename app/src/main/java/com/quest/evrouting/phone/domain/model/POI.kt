package com.quest.evrouting.phone.domain.model

data class POI(
    val id: String,
    val locationId: String,
    val location: Location,
    val information: Map<String, String>,
    val status: String,
    val connectors: List<Connector>
)
