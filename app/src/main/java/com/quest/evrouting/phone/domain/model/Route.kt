package com.quest.evrouting.phone.domain.model

import com.mapbox.geojson.Point

/**
 * Lớp model "sạch" đại diện cho một lộ trình trong tầng Domain.
 * Lớp này hoàn toàn không phụ thuộc vào bất kỳ thư viện bên ngoài nào.
 */
data class Route(
    /**
     * Dữ liệu hình học của lộ trình, được mã hóa dưới dạng chuỗi polyline6.
     * Tầng UI sẽ sử dụng chuỗi này để vẽ lên bản đồ.
     */
    val geometry: List<Point>,

    /**
     * Tổng quãng đường của lộ trình, tính bằng mét.
     */
    val distance: Double,

    /**
     * Tổng thời gian di chuyển ước tính, tính bằng giây.
     */
    val duration: Double,

    val chargePointsOnRoute: List<Int>
)
