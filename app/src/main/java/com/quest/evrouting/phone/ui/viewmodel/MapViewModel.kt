package com.quest.evrouting.phone.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.quest.evrouting.phone.domain.model.ChargePoint
import com.quest.evrouting.phone.domain.model.EVCar
import com.quest.evrouting.phone.domain.model.Place
import com.quest.evrouting.phone.domain.model.Route
import com.quest.evrouting.phone.domain.model.Vehicle
import com.quest.evrouting.phone.domain.repository.ChargePointRepository
import com.quest.evrouting.phone.domain.repository.DirectionsRepository
import com.quest.evrouting.phone.domain.repository.EVRouteRepository
import com.quest.evrouting.phone.domain.repository.GeocodingRepository
import com.quest.evrouting.phone.domain.usecase.GetChargePointsUseCase
import com.quest.evrouting.phone.domain.usecase.SimulateTripUseCase
import com.quest.evrouting.phone.util.Constants.MAPBOX_DRIVING_PROFILE
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// Trạng thái UI của màn hình Map
data class MapUiState(
    val isLoadingPoints: Boolean = false,
    val isFindingRoute: Boolean = false,
    val chargePoints: List<ChargePoint> = emptyList(),
    val errorMessage: String? = null,
    val centerCoordinate: Point = Point.fromLngLat(12.64630, 42.50530), // Tọa độ trung tâm từ TestActivity
    val origin: PlaceWithCoord? = null,
    val destination: PlaceWithCoord? = null
) {
    val isLoading: Boolean
        get() = isLoadingPoints || isFindingRoute
}

// Lớp mới để giữ cả Place gốc và tọa độ đã tìm được
data class PlaceWithCoord(val place: Place, val point: Point)

data class TripState(
    val vehicle: Vehicle,
    val currentLocation: Point,
    val isActive: Boolean = false,
    val isCharging: Boolean = false
)

// Lớp niêm phong cho các sự kiện UI (chỉ có một lần)
sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
}

class MapViewModel(
    private val getChargePointsUseCase: GetChargePointsUseCase,
//    private val directionsRepository: DirectionsRepository,
    private val evRouteRepository: EVRouteRepository,
    private val geocodingRepository: GeocodingRepository,
    private val simulateTripUseCase: SimulateTripUseCase,
) : ViewModel() {


    // --- STATE MANAGEMENT ---

    private val _route = mutableStateOf<Route?>(null)
    val route: State<Route?> = _route

    private val _uiState = mutableStateOf(MapUiState())
    val uiState: State<MapUiState> = _uiState

    // State riêng cho việc mô phỏng xe. Dùng StateFlow để phù hợp với coroutine flow.
    private val _tripState = MutableStateFlow(
        TripState(
            vehicle = EVCar(0.0, 0.0, 0.0),
            currentLocation = Point.fromLngLat(0.0, 0.0),
            isActive = false
        )
    )
    val tripState: StateFlow<TripState> = _tripState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    // --- JOB MANAGEMENT ---
    // Chỉ cần một job để quản lý toàn bộ quá trình tìm đường và mô phỏng.
    private var findAndSimulateJob: Job? = null


    init {
        Log.d("MapViewModel", "ViewModel initialized. Bắt đầu tải trạm sạc.")
        loadChargePoints()
    }

    private fun loadChargePoints() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingPoints = true, errorMessage = null)
            try {
                val points = getChargePointsUseCase()
                _uiState.value = _uiState.value.copy(isLoadingPoints = false, chargePoints = points)
                Log.d("MapViewModel", "Tải thành công ${points.size} trạm sạc.")
            } catch (e: IOException) {
                Log.e("MapViewModel", "Lỗi mạng khi tải trạm sạc.", e)
                _uiState.value = _uiState.value.copy(
                    isLoadingPoints = false,
                    errorMessage = "Lỗi mạng: Không thể tải danh sách trạm sạc."
                )
            } catch (e: Exception) {
                Log.e("MapViewModel", "Lỗi không xác định khi tải trạm sạc.", e)
                _uiState.value = _uiState.value.copy(
                    isLoadingPoints = false,
                    errorMessage = "Lỗi không xác định: ${e.message}"
                )
            }
        }
    }

    private suspend fun getCoordinates(origin: Place, destination: Place): Pair<Point, Point> {
        return coroutineScope {
            val originDeferred = async { geocodingRepository.getCoordinatesForPlaceName(origin.primaryText) }
            val destinationDeferred = async { geocodingRepository.getCoordinatesForPlaceName(destination.primaryText) }
            val originPoint = originDeferred.await()
            val destinationPoint = destinationDeferred.await()

            if (originPoint == null) throw Exception("Không thể tìm thấy tọa độ cho '${origin.primaryText}'")
            if (destinationPoint == null) throw Exception("Không thể tìm thấy tọa độ cho '${destination.primaryText}'")

            Log.d("DEBUG_ROUTE", "[ViewModel] Geocoding OK: Origin=${originPoint.longitude()},${originPoint.latitude()} | Dest=${destinationPoint.longitude()},${destinationPoint.latitude()}")
            Pair(originPoint, destinationPoint)
        }
    }


    // Xử lý khi người dùng nhấn vào một điểm sạc
    fun onChargePointClicked(chargePoint: ChargePoint) {
        viewModelScope.launch {
            // Sử dụng buildString để tạo một chuỗi thông tin chi tiết, dễ đọc.
            val message = buildString {
                append("Trạm: ${chargePoint.name}\n") // Tên trạm
                append("Địa chỉ: ${chargePoint.address}, ${chargePoint.town}\n") // Địa chỉ
                append("Tổng số cổng sạc: ${chargePoint.totalQuantity}\n") // Tổng số lượng
                append("----------\n")
                append("Các loại cổng sạc:\n")

                // Lặp qua danh sách các loại cổng sạc và thêm thông tin của từng loại
                if (chargePoint.connections.isEmpty()) {
                    append("- Không có thông tin chi tiết.")
                } else {
                    chargePoint.connections.forEach { conn ->
                        append("- ${conn.typeName} (${conn.powerKw} kW): ${conn.quantity} cổng\n")
                    }
                }
            }
            // Gửi sự kiện để UI hiển thị Toast với thông điệp đã tạo
            _uiEvent.send(UiEvent.ShowToast(message))
        }
    }

    // Xử lý khi người dùng nhấn nút "Recenter Map"
    fun onRecenterMapClicked(): Pair<CameraOptions, MapAnimationOptions> {
        val camera = cameraOptions {
            center(_uiState.value.centerCoordinate)
            zoom(11.0)
//            pitch(61.01)
//            bearing(15.77)
        }
        val animationOptions = MapAnimationOptions.Builder()
            .duration(3000L)
            .build()
        return Pair(camera, animationOptions)
    }

//    private fun findRoute(points: List<Point>) {
//        // >>> THÊM LOG Ở ĐÂY
//        Log.d("MapViewModel", "findRoute called with ${points.size} points.")
//
//        viewModelScope.launch {
//            _uiState.value = _uiState.value.copy(isFindingRoute = true, errorMessage = null)
//            _route.value = null // Xóa lộ trình cũ trước khi tìm lộ trình mới
//            try {
//                // >>> THÊM LOG Ở ĐÂY
//                Log.d("MapViewModel", "Calling directionsRepository.getDirections...")
//
//                // Gọi repository để lấy dữ liệu lộ trình
//                val foundRoute = directionsRepository.getDirections(
//                    points = points,
//                    profile = MAPBOX_DRIVING_PROFILE // Hoặc "walking", "cycling"
//                )
//                // >>> THÊM LOG Ở ĐÂY
//                Log.d("MapViewModel", "Successfully found route. Geometry length: ${foundRoute?.geometry?.length}")
//
//                _route.value = foundRoute // Cập nhật lộ trình mới
//            } catch (e: IOException) {
//                // >>> THÊM LOG Ở ĐÂY
//                Log.e("MapViewModel", "Network error finding route.", e)
//
//                _uiState.value = _uiState.value.copy(
//                    errorMessage = "Network error: Could not find the route."
//                )
//                e.printStackTrace()
//            } catch (e: Exception) {
//                // >>> THÊM LOG Ở ĐÂY
//                Log.e("MapViewModel", "An unexpected error occurred while finding route.", e)
//
//                _uiState.value = _uiState.value.copy(
//                    errorMessage = "Failed to find route: ${e.message}"
//                )
//                e.printStackTrace()
//            } finally {
//                // Dù thành công hay thất bại, cũng kết thúc trạng thái loading.
//                _uiState.value = _uiState.value.copy(isFindingRoute = false)
//            }
//        }
//    }

//    fun findSequentialRoute() {
//        // >>> THÊM LOG Ở ĐÂY
//        Log.d("MapViewModel", "findSequentialRoute triggered.")
//
//        val allPoints = _uiState.value.chargePoints
//        // Cần ít nhất 2 điểm để tạo thành một lộ trình.
//        if (allPoints.size < 2) {
//            // >>> THÊM LOG Ở ĐÂY
//            Log.w("MapViewModel", "Not enough points to create a route. Found only ${allPoints.size} points.")
//
//            viewModelScope.launch {
//                _uiEvent.send(UiEvent.ShowToast("Not enough charge points to create a route."))
//            }
//            return
//        }
//        // Sắp xếp các điểm theo ID để có một lộ trình nhất quán.
//        val pointsInOriginalOrder = allPoints.map { it.point }
//        findRoute(pointsInOriginalOrder)
//    }


    // --- HÀM MỚI ĐỂ XỬ LÝ YÊU CẦU TỪ SEARCHSCREEN ---
    fun findRouteFromPlaces(origin: Place, destination: Place) {
        Log.d("DEBUG_ROUTE", "[ViewModel] Nhận yêu cầu: '${origin.primaryText}' -> '${destination.primaryText}'")

        // Hủy job cũ trước khi bắt đầu job mới để tránh chạy song song.
        findAndSimulateJob?.cancel()

        findAndSimulateJob = viewModelScope.launch {
            // Đặt lại các trạng thái trước khi bắt đầu
            _uiState.value = _uiState.value.copy(isFindingRoute = true, errorMessage = null, origin = null, destination = null)
            _route.value = null
            _tripState.value = _tripState.value.copy(isActive = false)

            try {
                // 1. Geocoding song song
                val (originPoint, destinationPoint) = getCoordinates(origin, destination)
                _uiState.value = _uiState.value.copy(
                    origin = PlaceWithCoord(origin, originPoint),
                    destination = PlaceWithCoord(destination, destinationPoint)
                )




                // 2. Tạo xe demo
                // XEM LẠI THÔNG SỐ
                val demoCar = EVCar(totalPowerKwh = 80.0, currentPowerKwh = 75.0, averageSpeedKmh = 60.0)






                // 3. Gọi repository tìm đường
                val newRoute = evRouteRepository.getRoute(
                    originLon = originPoint.longitude(),
                    originLat = originPoint.latitude(),
                    destinationLon = destinationPoint.longitude(),
                    destinationLat = destinationPoint.latitude(),
                    powerKwh = demoCar.totalPowerKwh,
                    currentPower = demoCar.currentPowerKwh
                )

                if (newRoute != null && newRoute.geometry.isNotEmpty()) {
                    _route.value = newRoute
                    _uiState.value = _uiState.value.copy(isFindingRoute = false)
                    Log.d("DEBUG_ROUTE", "[ViewModel] Đã nhận lộ trình, bắt đầu lắng nghe mô phỏng từ UseCase.")

                    // 4. GỌI USECASE VÀ LẮNG NGHE KẾT QUẢ
                    simulateTripUseCase.execute(newRoute, demoCar, destinationPoint)
                        .collect { newState ->
                            _tripState.value = newState
                        }

                    Log.d("DEBUG_ROUTE", "[ViewModel] Flow mô phỏng đã kết thúc.")

                } else {
                    throw Exception("Backend không trả về lộ trình hợp lệ.")
                }

            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) {
                    Log.d("DEBUG_ROUTE", "[ViewModel] Job tìm đường và mô phỏng đã bị hủy.")
                    // Nếu coroutine bị hủy bởi người dùng, không hiển thị lỗi
                } else {
                    Log.e("DEBUG_ROUTE", "[ViewModel] Lỗi trong quá trình tìm đường: ${e.message}", e)
                    _uiState.value = _uiState.value.copy(errorMessage = "Lỗi: ${e.message}")
                }
            } finally {
                // Dù thành công hay thất bại, cũng kết thúc trạng thái loading.
                _uiState.value = _uiState.value.copy(isFindingRoute = false)
            }
        }
    }




}
