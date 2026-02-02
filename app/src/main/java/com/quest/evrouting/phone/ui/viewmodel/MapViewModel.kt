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
import com.quest.evrouting.phone.domain.model.EVCar
import com.quest.evrouting.phone.domain.model.POI
import com.quest.evrouting.phone.domain.model.Place
import com.quest.evrouting.phone.domain.model.Path
import com.quest.evrouting.phone.domain.model.Vehicle
import com.quest.evrouting.phone.domain.repository.EVRouteRepository
import com.quest.evrouting.phone.domain.repository.GeocodingRepository
import com.quest.evrouting.phone.domain.usecase.GetPoisUseCase
import com.quest.evrouting.phone.domain.usecase.SimulateTripUseCase
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

data class MapUiState(
    val isLoadingPoints: Boolean = false,
    val isFindingRoute: Boolean = false,
    val pois: List<POI> = emptyList(),
    val errorMessage: String? = null,
    val centerCoordinate: Point = Point.fromLngLat(12.64630, 42.50530), // Tọa độ trung tâm mặc định
    val origin: PlaceWithCoord? = null,
    val destination: PlaceWithCoord? = null
) {
    val isLoading: Boolean
        get() = isLoadingPoints || isFindingRoute
}

data class PlaceWithCoord(val place: Place, val point: Point)

data class TripState(
    val vehicle: Vehicle,
    val currentLocation: Point,
    val isActive: Boolean = false,
    val isCharging: Boolean = false
)

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
}

class MapViewModel(
    private val getPoisUseCase: GetPoisUseCase,
    private val evRouteRepository: EVRouteRepository,
    private val geocodingRepository: GeocodingRepository,
    private val simulateTripUseCase: SimulateTripUseCase,
) : ViewModel() {


    // --- STATE MANAGEMENT ---

    private val _route = mutableStateOf<Path?>(null)
    val route: State<Path?> = _route

    private val _uiState = mutableStateOf(MapUiState())
    val uiState: State<MapUiState> = _uiState

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


    // Chỉ cần một job để quản lý toàn bộ quá trình tìm đường và mô phỏng.
    private var findAndSimulateJob: Job? = null


    init {
        Log.d("MapViewModel", "ViewModel initialized. Bắt đầu tải trạm sạc.")
        loadPois()
    }

    private fun loadPois() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingPoints = true, errorMessage = null)
            try {
                val points = getPoisUseCase()
                _uiState.value = _uiState.value.copy(isLoadingPoints = false, pois = points)
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


    fun onPoiClicked(poi: POI) {
        viewModelScope.launch {
            val message = buildString {
                append("ID: ${poi.id}\n")
                append("Trạng thái: ${poi.status}\n")
                append("Địa chỉ: ${poi.information["address"]}, ${poi.information["city"]}\n")
                append("----------\n")
                append("Các loại cổng sạc:\n")

                if (poi.connectors.isEmpty()) {
                    append("- Không có thông tin chi tiết.")
                } else {
                    poi.connectors.forEach { conn ->
                        append("- Loại: ${conn.connectorType}, Định dạng: ${conn.connectorFormat}\n")
                        append("  Công suất: ${conn.maxElectricPower} W, Nguồn: ${conn.powerType}\n")
                    }
                }
            }
            _uiEvent.send(UiEvent.ShowToast(message))
        }
    }

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

    fun findRouteFromPlaces(origin: Place, destination: Place) {
        Log.d("DEBUG_ROUTE", "[ViewModel] Nhận yêu cầu: '${origin.primaryText}' -> '${destination.primaryText}'")

        // Hủy job cũ trước khi bắt đầu job mới để tránh chạy song song.
        findAndSimulateJob?.cancel()

        findAndSimulateJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isFindingRoute = true, errorMessage = null, origin = null, destination = null)
            _route.value = null
            _tripState.value = _tripState.value.copy(isActive = false)

            try {
                // Geocoding song song
                val (originPoint, destinationPoint) = getCoordinates(origin, destination)
                _uiState.value = _uiState.value.copy(
                    origin = PlaceWithCoord(origin, originPoint),
                    destination = PlaceWithCoord(destination, destinationPoint)
                )
                // Tạo xe demo
                val demoCar = EVCar(totalPowerKwh = 80.0, currentPowerKwh = 75.0, averageSpeedKmh = 60.0)

                val originLocation = com.quest.evrouting.phone.domain.model.Location(
                    latitude = originPoint.latitude(),
                    longitude = originPoint.longitude()
                )
                val destinationLocation = com.quest.evrouting.phone.domain.model.Location(
                    latitude = destinationPoint.latitude(),
                    longitude = destinationPoint.longitude()
                )

                val newRoute = evRouteRepository.getRoute(
                    current = originLocation,
                    target = destinationLocation
                )

                if (newRoute != null && newRoute.decodedPolyline.isNotEmpty()) {
                    _route.value = newRoute
                    _uiState.value = _uiState.value.copy(isFindingRoute = false)
                    Log.d("DEBUG_ROUTE", "[ViewModel] Đã nhận lộ trình, bắt đầu lắng nghe mô phỏng từ UseCase.")

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
                } else {
                    Log.e("DEBUG_ROUTE", "[ViewModel] Lỗi trong quá trình tìm đường: ${e.message}", e)
                    _uiState.value = _uiState.value.copy(errorMessage = "Lỗi: ${e.message}")
                }
            } finally {
                _uiState.value = _uiState.value.copy(isFindingRoute = false)
            }
        }
    }
}
