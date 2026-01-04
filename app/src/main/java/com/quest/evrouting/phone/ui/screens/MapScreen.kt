package com.quest.evrouting.phone.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.maps.MapboxDelicateApi
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.layers.generated.LineLayer
import com.mapbox.maps.extension.compose.style.standard.LightPresetValue
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.extension.compose.style.standard.ThemeValue
import com.mapbox.maps.extension.compose.style.standard.rememberStandardStyleState
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.quest.evrouting.phone.R
import com.quest.evrouting.phone.configuration.AppConfig
import com.quest.evrouting.phone.ui.viewmodel.MapViewModel
import com.mapbox.geojson.LineString
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.quest.evrouting.phone.domain.model.Place
import com.quest.evrouting.phone.ui.components.SearchTopBar
import com.quest.evrouting.phone.ui.viewmodel.UiEvent

@OptIn(MapboxDelicateApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(factory = AppConfig.mapViewModelFactory),
    onSearchClick: () -> Unit,
    newOrigin: Place?,
    newDestination: Place?,
    onNewRouteHandled: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState
    val route by viewModel.route
    val tripState by viewModel.tripState.collectAsState()
    val uiEvent = viewModel.uiEvent


    // Cài đặt camera ban đầu và điều khiển nó
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(11.0)
            center(uiState.centerCoordinate) // Lấy tọa độ từ ViewModel
            pitch(61.01)
            bearing(15.77)
        }
    }

    LaunchedEffect(key1 = true) {
        uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(newOrigin, newDestination) {
        // Chỉ thực hiện khi CẢ HAI điểm đều không null
        if (newOrigin != null && newDestination != null) {
            // --- DEBUG: BƯỚC 1 (Đã cập nhật) ---
            // In ra thông tin của Place nhận được để kiểm tra
            Log.d("DEBUG_ROUTE", "[MapScreen] Nhận được yêu cầu tìm đường.")
            // Thay `newOrigin.coordinate.latitude()` bằng `newOrigin.latitude`
            Log.d("DEBUG_ROUTE", " -> Origin: ${newOrigin.primaryText}")
            Log.d("DEBUG_ROUTE", " -> Origin: ${newOrigin.secondaryText}")
            Log.d("DEBUG_ROUTE", " -> Destination: ${newDestination.primaryText}")
            Log.d("DEBUG_ROUTE", " -> Destination: ${newDestination.secondaryText}")

            // Gọi hàm trong ViewModel để bắt đầu tìm đường thực sự
            viewModel.findRouteFromPlaces(newOrigin, newDestination)

            // Báo cho navigation biết đã xử lý xong
            onNewRouteHandled()
        }
    }

//    Scaffold(
//        // Thêm nút FloatingActionButton
//        floatingActionButton = {
//            FloatingActionButton(
//                modifier = Modifier.padding(16.dp),
//                onClick = {
//                    val (cameraOptions, animationOptions) = viewModel.onRecenterMapClicked()
//                    mapViewportState.flyTo(cameraOptions, animationOptions)
//                },
//                shape = RoundedCornerShape(16.dp),
//            ) {
//                Text(modifier = Modifier.padding(10.dp), text = "Recenter Map")
//            }
//        }
//    ) { innerPadding ->
    Scaffold(
        // Thêm các nút FloatingActionButton
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                // Nút 1: Tìm lộ trình tuần tự
                if (newOrigin != null && newDestination != null) {
                    FloatingActionButton(
//                    onClick = { viewModel.findSequentialRoute() },
                        onClick = { viewModel.findRouteFromPlaces(newOrigin, newDestination) },
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = stringResource(R.string.find_sequential_route_description)
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                }

                // Nút 2: Căn giữa bản đồ
                FloatingActionButton(
                    onClick = {
                        val (cameraOptions, animationOptions) = viewModel.onRecenterMapClicked()
                        mapViewportState.flyTo(cameraOptions, animationOptions)
                    },
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.recenter_map_description)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
//            contentAlignment = Alignment.Center
        ) {
            MapboxMap(
                Modifier.fillMaxSize(),
                mapViewportState = mapViewportState,
                style = {
                    MapboxStandardStyle(
                        standardStyleState = rememberStandardStyleState {
                            configurationsState.apply {
                                lightPreset = LightPresetValue.DAWN
                                theme = ThemeValue.FADED
                            }
                        }
                    )
                }
            ) {
                // 1. Vẽ các điểm sạc
                val markerDefault = rememberIconImage(R.drawable.charging_station) // Icon mặc định
                val markerOnRoute = rememberIconImage(R.drawable.baseline_location_on_24) // Icon cho điểm trên lộ trình

                // Lấy danh sách các điểm trên lộ trình để so sánh
                val pointsOnRoute = route?.geometry ?: emptyList()

                uiState.chargePoints.forEach { chargePoint ->
                    PointAnnotation(point = chargePoint.point) {
                        // Chọn icon dựa trên việc điểm đó có nằm trên lộ trình không
                        iconImage = if (pointsOnRoute.contains(chargePoint.point)) markerOnRoute else markerDefault

                        iconSize = 0.2

                        interactionsState.onClicked {
                            viewModel.onChargePointClicked(chargePoint)
                            true
                        }
                    }
                }

                // 2. Vẽ lộ trình nếu có
                route?.let { aRoute ->
                    PolylineAnnotation(points = aRoute.geometry) {
                        // Tất cả cấu hình giao diện nằm trong khối 'init' này
                        lineColor = Color.Blue  // Gán màu trực tiếp
                        lineWidth = 6.0         // Gán độ rộng
                        lineOpacity = 0.8

                        // Bạn cũng có thể thêm các cấu hình khác nếu cần
                         lineJoin = LineJoin.ROUND
                    }
                }




                val carIcon = rememberIconImage(R.drawable.car2)
                val originIcon = rememberIconImage(R.drawable.car)
                val destinationIcon = rememberIconImage(R.drawable.pin)

                if (tripState.isActive) {
                    PointAnnotation(point = tripState.currentLocation) // Lấy vị trí từ TripState
                    {
                        iconImage = carIcon
                        iconSize = 0.2
                    }
                }

                // 2. VẼ ĐIỂM ORIGIN (NẾU CÓ)
                uiState.origin?.let { placeWithCoord ->
                    PointAnnotation(point = placeWithCoord.point){
                        iconImage = originIcon
                        iconSize = 0.2
                    }
                }

                // 3. VẼ ĐIỂM DESTINATION (NẾU CÓ)
                uiState.destination?.let { placeWithCoord ->
                    PointAnnotation(point = placeWithCoord.point){
                        iconImage = destinationIcon
                        iconSize = 0.2
                    }
                }
            }

            SearchTopBar(
                onSearchClick = onSearchClick,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    color = Color.Red
                )
            }
        }
    }
}



