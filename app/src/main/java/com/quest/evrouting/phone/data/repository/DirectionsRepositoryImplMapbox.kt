package com.quest.evrouting.phone.data.repository

//import com.mapbox.geojson.Point
//import com.quest.evrouting.phone.data.remote.api.mapbox.MapboxApiClient
//import com.quest.evrouting.phone.data.remote.api.mapbox.directions.toRoute
//import com.quest.evrouting.phone.domain.model.Route
//import com.quest.evrouting.phone.domain.repository.DirectionsRepository
//import com.quest.evrouting.phone.util.Constants
//
//class DirectionsRepositoryImplMapbox : DirectionsRepository{
//    private val apiService = MapboxApiClient.directionsService
//
//    // Lưu ý: Đã xóa try-catch
//    override suspend fun getDirections(points: List<Point>, profile: String): Route? {
//        // Kiểm tra cơ bản
//        if (points.size < 2) return null
//
//        val coordinates = points.joinToString(";") { "${it.longitude()},${it.latitude()}" }
//
//        // Gọi API trực tiếp.
//        // Nếu mạng lỗi hoặc API lỗi, Retrofit sẽ tự động NÉM (THROW) Exception ra ngoài.
//        val responseDto = apiService.getDirections(
//            profile = profile,
//            coordinates = coordinates,
//            accessToken = Constants.MAPBOX_ACCESS_TOKEN
//        )
//
//        // Kiểm tra xem Mapbox có trả về danh sách route rỗng không
//        if (responseDto.routes.isEmpty()) {
//            throw Exception("Mapbox API returned no routes (Empty list).")
//        }
//
//        // Ánh xạ sang lớp Route của domain
//        return responseDto.routes.first().toRoute()
//    }
//}