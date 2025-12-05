package com.quest.evrouting.apiservice.ocm.model

import com.quest.evrouting.libservice.geometry.domain.model.Point
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.quest.evrouting.libservice.geometry.service.GeohashService

@Serializable
data class AddressInfo(
    @SerialName("ID") val id: Int,
    @SerialName("Title") val title: String? = null,
    @SerialName("AddressLine1") val addressLine1: String? = null,
    @SerialName("Town") val town: String? = null,
    @SerialName("Postcode") val postcode: String? = null,
    @SerialName("Country") val country: Country,
    @SerialName("Latitude") val latitude: Double,
    @SerialName("Longitude") val longitude: Double,
    @SerialName("AccessComments") val accessComments: String? = null,

    @Transient // bỏ qua tìm kiếm thuộc tính này trong JSON
    val geohash12: Long = GeohashService.encode(Point(longitude, latitude), 60).value
)