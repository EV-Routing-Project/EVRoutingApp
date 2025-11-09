package com.quest.evrounting.data.remote

import com.quest.evrounting.data.model.staticc.ChargePoint // Import các data class của bạn
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Header

/**
 * Định nghĩa các endpoint của Open Charge Map API.
 */
interface OcmApiService {

    // Lấy danh sách các POI (Point of Interest - Trạm sạc)
    // Ví dụ: https://api.openchargemap.io/v3/poi/?output=json&countrycode=IT
    // Vì không set @param compact=true (mặc định là False) nên mỗi POI trả về sẽ
    //      đi kèm với bảng tham chiếu (các thuộc tính Object)
    @GET("poi")
    suspend fun getPois(
        // API đã được xử lý trong RetrofitClient
        @Query("output") output: String = "json",
        @Query("countrycode") countryCode: String = "IT",
    ): Response<List<ChargePoint>>
}
