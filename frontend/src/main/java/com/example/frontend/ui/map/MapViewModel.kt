//// file: frontend/src/main/java/com/example/frontend/ui/map/MapViewModel.kt
//package com.example.frontend.ui.map
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.frontend.repository.DirectionsRepository
//import com.example.frontend.repository.MapboxDirectionsRepository
//import com.mapbox.geojson.LineString
//import com.mapbox.geojson.Point
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//class MapViewModel : ViewModel() {
//    // Khởi tạo Repository. Sau này có thể dùng Hilt/Koin để inject
//    private val directionsRepository: DirectionsRepository = MapboxDirectionsRepository()
//
//    // StateFlow để chứa dữ liệu đường đi (LineString)
//    private val _route = MutableStateFlow<LineString?>(null)
//    val route = _route.asStateFlow()
//
//    // Hàm được gọi từ UI để bắt đầu tìm đường
//    fun findDirections(origin: Point, destination: Point) {
//        viewModelScope.launch {
//            _route.value = null // Xóa đường cũ trước khi tìm đường mới
//            _route.value = directionsRepository.getDirectionsRoute(origin, destination)
//        }
//    }
//}
//