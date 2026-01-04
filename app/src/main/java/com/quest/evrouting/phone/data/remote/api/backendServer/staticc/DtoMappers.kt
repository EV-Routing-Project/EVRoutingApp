package com.quest.evrouting.phone.data.remote.api.backendServer.staticc

import com.quest.evrouting.phone.data.remote.api.backendServer.staticc.dto.response.ChargePointResponseDto
import com.quest.evrouting.phone.domain.model.ChargePoint
import com.quest.evrouting.phone.domain.model.Connection

fun ChargePointResponseDto.toChargePoint(): ChargePoint {
    val properties = this.properties
    val totalQuantity = properties.connections.sumOf { it.quantity }
    val location = this.geometry.toMapboxPoint()

    // Chuyển đổi danh sách Connection DTO sang Connection Domain
    val domainConnections = properties.connections.map { connectionDto ->
        Connection(
            typeName = connectionDto.connectionTypeName,
            currentType = connectionDto.currentTypeName,
            powerKw = connectionDto.powerKw,
            quantity = connectionDto.quantity
        )
    }

    return ChargePoint(
        id = properties.id,
        name = properties.name,
        address = properties.address,
        town = properties.town,
        totalQuantity = totalQuantity,
        point = location,
        connections = domainConnections
    )
}