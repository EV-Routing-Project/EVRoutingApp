package com.quest.evrouting.phone.data.remote.api.backendServer.directions

import com.mapbox.geojson.LineString
import com.quest.evrouting.phone.domain.model.EVSE
import com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.request.Location as LocationDto
import com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.response.Path as PathDto
import com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.response.Node as NodeDto
import com.quest.evrouting.phone.domain.model.Path
import com.quest.evrouting.phone.domain.model.Location


fun PathDto.toPath(): Path {
    // Tham số 6 là độ chính xác (precision) cho polyline, giá trị 6 là phổ biến cho các API định tuyến.
    val decodedPolyline = LineString.fromPolyline(this.encodedPolyline, 6).coordinates()

    if (decodedPolyline.isEmpty()) {
        throw IllegalArgumentException("Không thể giải mã polyline từ API: ${this.encodedPolyline}")
    }

    return Path(
        decodedPolyline = decodedPolyline,
        length = this.length,
        time = this.time
    )
}

fun NodeDto.toEVSE(): EVSE {
    return EVSE(
        id = this.id,
        groupId = this.groupId,
        time = this.time,
        preBatteryPercent = this.preBatteryPercent,
        batteryPercent = this.batteryPercent
    )
}

fun LocationDto.toLocation(): Location {
    return Location(
        latitude = this.lat,
        longitude = this.lon
    )
}

fun Location.toLocationDto(): LocationDto {
    return LocationDto(
        lat = this.latitude,
        lon = this.longitude
    )
}