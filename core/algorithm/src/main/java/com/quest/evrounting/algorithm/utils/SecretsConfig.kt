package com.quest.evrounting.algorithm.utils

import com.quest.evrounting.apiservice.mapbox.MapboxApiClient
import kotlinx.coroutines.runBlocking

/**
 * Lớp này tạm thời được dùng để chứa hàm main, phục vụ việc test nhanh API.
 */
class SecretsConfig {
    companion object {
        private const val MAPBOX_TEST_TOKEN = "Key"
        @JvmStatic
        fun main(args: Array<String>) {
            println("Bắt đầu bài test gọi API Mapbox Directions...")
            runBlocking {
                try {
                    val response = MapboxApiClient.directionsService.getDirections(
                        profile = "driving-traffic",
                        coordinates = "106.701755,10.776649;106.695383,10.771688",
                        accessToken = MAPBOX_TEST_TOKEN
                    )

                    // Kiểm tra kết quả
                    if (response.isSuccessful) {
                        val directions = response.body()
                        val route = directions?.routes?.firstOrNull()

                        println("\n✅ GỌI API THÀNH CÔNG!")
                        println("=========================================")
                        println("Mã phản hồi: ${response.code()}")
                        println("Tuyến đường: ${route?.legs?.firstOrNull()?.summary}")
                        println("Khoảng cách: ${route?.distance} mét")
                        println("Thời gian dự kiến: ${route?.duration?.div(60)?.toInt()} phút")
                        println("=========================================")

                    } else {
                        println("\n❌ GỌI API THẤT BẠI!")
                        println("=========================================")
                        println("Mã lỗi: ${response.code()}")
                        println("Thông điệp lỗi: ${response.errorBody()?.string()}")
                        println("=========================================")
                    }

                } catch (e: Exception) {
                    println("\n🚨 ĐÃ XẢY RA LỖI NGOẠI LỆ!")
                    println("=========================================")
                    e.printStackTrace()
                    println("=========================================")
                }
            }

            println("\nBài test kết thúc.")
        }
    }
}
