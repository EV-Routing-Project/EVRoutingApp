package com.quest.evrounting.apiservice.ocm.pois

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Định nghĩa các endpoint của Open Charge Map API.
 */
interface PoisApiService {

    // Lấy danh sách các POI (Point of Interest - Trạm sạc)
    // Ví dụ: https://api.openchargemap.io/v3/poi/?output=json&countrycode=IT
    // Vì không set @param compact=true (mặc định là False) nên mỗi POI trả về sẽ
    //      đi kèm với bảng tham chiếu (các thuộc tính Object)
    @GET("poi")
    suspend fun getPois(
        // API đã được xử lý trong RetrofitClient
        @Query("output") output: String = "json",
        @Query("countrycode") countryCode: String = "IT",
        @Query("key") apiKey: String,
    ): Response<List<PoisResponse>>
}
