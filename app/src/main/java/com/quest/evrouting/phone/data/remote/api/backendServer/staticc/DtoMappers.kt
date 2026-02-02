package com.quest.evrouting.phone.data.remote.api.backendServer.staticc

import com.quest.evrouting.phone.data.remote.api.backendServer.staticc.dto.response.Connector as ConnectorDto
import com.quest.evrouting.phone.data.remote.api.backendServer.staticc.dto.response.Location as LocationDto
import com.quest.evrouting.phone.data.remote.api.backendServer.staticc.dto.response.POI as PoiDto
import com.quest.evrouting.phone.domain.model.Connector
import com.quest.evrouting.phone.domain.model.Location
import com.quest.evrouting.phone.domain.model.POI

fun PoiDto.toDomainPoi(): POI {
    return POI(
        id = this.id,
        locationId = this.locationId,
        location = this.location.toDomainLocation(),
        information = info,
        status = this.status,
        connectors = this.connectors.map { it.toDomainConnector() }
    )
}

fun LocationDto.toDomainLocation(): Location {
    return Location(
        latitude = this.lat,
        longitude = this.lon
    )
}

fun ConnectorDto.toDomainConnector(): Connector {
    return Connector(
        connectorType = this.connectorType,
        powerType = this.powerType,
        connectorFormat = this.connectorFormat,
        maxElectricPower = this.maxElectricPower
    )
}
