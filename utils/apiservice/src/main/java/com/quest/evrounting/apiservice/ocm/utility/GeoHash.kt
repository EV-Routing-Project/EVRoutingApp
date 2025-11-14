package com.quest.evrounting.apiservice.ocm.utility

object GeoHash {
    /**
     * Mã hóa tọa độ thành Geohash dưới dạng số nguyên Long 64-bit (chỉ sử dụng 60 bit cho precision 12).
     * * @param latitude Vĩ độ (-90 đến 90).
     * @param longitude Kinh độ (-180 đến 180).
     * @param precision Độ dài Geohash mong muốn (12 ký tự = 60 bit).
     * @return Giá trị Geohash dưới dạng Long (BIGINT).
     */
    fun encodeHashToLong(latitude: Double, longitude: Double, precision: Int): Long {
        // 1. Tính tổng số bit cần thiết (12 ký tự * 5 bit/ký tự = 60 bit)
        val totalBits = precision * 5

        // 2. Khởi tạo khoảng tọa độ
        var latRange = doubleArrayOf(-90.0, 90.0)
        var lonRange = doubleArrayOf(-180.0, 180.0)

        var geohashLong: Long = 0L // Giá trị Long cuối cùng
        var isEven = true // Bắt đầu với Kinh độ (lượt chẵn)

        for (i in 0 until totalBits) {

            var bitValue = 0 // Giá trị bit hiện tại (0 hoặc 1)

            // 3. Quyết định trục và tính toán bit
            if (isEven) {
                // Xử lý Kinh độ (Long)
                val mid = (lonRange[0] + lonRange[1]) / 2.0
                if (longitude > mid) {
                    bitValue = 1
                    lonRange[0] = mid
                } else {
                    bitValue = 0
                    lonRange[1] = mid
                }
            } else {
                // Xử lý Vĩ độ (Lat)
                val mid = (latRange[0] + latRange[1]) / 2.0
                if (latitude > mid) {
                    bitValue = 1
                    latRange[0] = mid
                } else {
                    bitValue = 0
                    latRange[1] = mid
                }
            }

            // 4. Ghi bit vào giá trị Long
            // Dịch Long hiện tại sang trái 1 bit, sau đó thêm bitValue (0 hoặc 1)
            geohashLong = (geohashLong shl 1) or bitValue.toLong()

            // 5. Đổi trục
            isEven = !isEven
        }

        // Giá trị Long 60 bit đã sẵn sàng
        return geohashLong
    }
}