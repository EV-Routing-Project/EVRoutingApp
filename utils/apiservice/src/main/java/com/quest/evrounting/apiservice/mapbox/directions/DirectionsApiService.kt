package com.quest.evrounting.apiservice.mapbox.directions

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DirectionsApiService {
    /**
     * Lấy thông tin chỉ đường giữa các điểm tọa độ.
     * Đây là một suspend function, cho phép gọi bất đồng bộ bằng coroutines.
     *
     * @param profile Kiểu di chuyển (vd: "driving-traffic", "walking", "cycling").
     * @param coordinates Chuỗi tọa độ có định dạng "kinh_do,vi_do;kinh_do,vi_do;...".
     * @param accessToken Token truy cập Mapbox của bạn.
     * @return Một đối tượng Response<DirectionsResponse> cho phép kiểm tra thành công hay thất bại.
     */
    @GET("directions/v5/mapbox/{profile}/{coordinates}")
    suspend fun getDirections(
        @Path("profile") profile: String,
        @Path("coordinates") coordinates: String,
        @Query("alternatives") alternatives: Boolean = true,
        @Query("geometries") geometries: String = "geojson",
        @Query("overview") overview: String = "full",
        @Query("steps") steps: Boolean = true,
        @Query("access_token") accessToken: String
    ) : Response<DirectionsResponse>
}