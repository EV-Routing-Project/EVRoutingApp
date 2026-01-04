package com.quest.evrouting.phone.domain.repository

import com.mapbox.geojson.Point

/**
 * Interface (hợp đồng) cho một repository có chức năng Geocoding.
 * Nhiệm vụ của nó là chuyển đổi một chuỗi tên địa điểm (place name) thành tọa độ địa lý (Point).
 */
interface GeocodingRepository {
    /**
     * Tìm kiếm và lấy tọa độ cho một tên địa điểm.
     *
     * @param placeName Chuỗi văn bản chứa tên địa điểm cần tìm (ví dụ: "Quận 10", "Sân bay Tân Sơn Nhất").
     * @return Một đối tượng Point chứa kinh độ và vĩ độ, hoặc null nếu không tìm thấy địa điểm.
     * @throws Exception nếu có lỗi mạng hoặc lỗi từ API.
     */
    suspend fun getCoordinatesForPlaceName(placeName: String): Point?
}
