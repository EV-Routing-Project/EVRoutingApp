package com.quest.evrouting.data.remote

import com.quest.evrouting.apiservice.ocm.OcmApiClient
import com.quest.evrouting.apiservice.ocm.pois.PoisResponse


object OcmApiCaller {
    /**
     * Gọi API của Open Charge Map để lấy danh sách các trạm sạc (POIs).
     *
     * @param apiKey Khóa API của bạn để xác thực với OCM.
     * @return Một đối tượng `Result<List<PoisResponse>>` chứa danh sách trạm sạc nếu thành công,
     *         hoặc một Exception nếu có lỗi xảy ra.
     */

    //  Thay vì trả về List<PoisResponse>? (nullable list), hàm này trả về một Result<List<PoisResponse>>
    suspend fun fetchChargePoints(apiKey: String): Result<List<PoisResponse>> {
        return try {
            // Gọi đến service đã được định nghĩa trong OcmApiClient
            val response = OcmApiClient.poisServices.getPois(
                apiKey = apiKey,
            )

            // Kiểm tra xem cuộc gọi có thành công về mặt HTTP không (mã 2xx)
            if (response.isSuccessful) {
                val poisList = response.body()

                if (poisList != null) {
                    // Thành công: trả về danh sách bên trong một đối tượng Result.success
                    println("✅ OcmApiCaller: Lấy thành công ${poisList.size} trạm sạc từ API.")
                    Result.success(poisList)
                } else {
                    // Thành công nhưng không có dữ liệu -> Coi như một dạng lỗi
                    println("⚠️ OcmApiCaller: Gọi API thành công nhưng không có dữ liệu.")
                    Result.failure(Exception("API call successful but body is null."))
                }
            } else {
                // Lỗi từ server (4xx, 5xx): trả về một Exception với thông điệp lỗi
                val errorMsg = "API call failed with code ${response.code()}: ${response.errorBody()?.string()}"
                println("❌ OcmApiCaller: $errorMsg")
                Result.failure(Exception(errorMsg))
            }

        } catch (e: Exception) {
            // Lỗi mạng, timeout, parse JSON...: trả về Exception đã bắt được
            println("🚨 OcmApiCaller: Đã xảy ra lỗi ngoại lệ: ${e.message}")
            Result.failure(e)
        }
    }
}
