package com.quest.evrouting.phone.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.mapbox.maps.extension.compose.style.standard.LightPresetValue
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.extension.compose.style.standard.ThemeValue
import com.mapbox.maps.extension.compose.style.standard.rememberStandardStyleState
import com.quest.evrouting.phone.R
import com.quest.evrouting.phone.configuration.AppConfig
import com.quest.evrouting.phone.ui.viewmodel.MapViewModel
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.gson.JsonPrimitive
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.quest.evrouting.phone.domain.model.Place
import com.quest.evrouting.phone.ui.components.SearchTopBar
import com.quest.evrouting.phone.ui.viewmodel.UiEvent
import com.quest.evrouting.phone.util.drawableToBitmap


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
        if (newOrigin != null && newDestination != null) {
            Log.d("DEBUG_ROUTE", "[MapScreen] Nhận được yêu cầu tìm đường.")
            Log.d("DEBUG_ROUTE", " -> Origin: ${newOrigin.primaryText}")
            Log.d("DEBUG_ROUTE", " -> Origin: ${newOrigin.secondaryText}")
            Log.d("DEBUG_ROUTE", " -> Destination: ${newDestination.primaryText}")
            Log.d("DEBUG_ROUTE", " -> Destination: ${newDestination.secondaryText}")

            viewModel.findRouteFromPlaces(newOrigin, newDestination)
            onNewRouteHandled()
        }
    }

    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                // Nút 1: Tìm lộ trình tuần tự
                if (newOrigin != null && newDestination != null) {
                    FloatingActionButton(
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
//                val markerDefault = rememberIconImage(R.drawable.charging_station) // Icon mặc định
//                val markerOnRoute = rememberIconImage(R.drawable.baseline_location_on_24) // Icon cho điểm trên lộ trình
//
//                val pointsOnRoute = route?.decodedPolyline?.toSet() ?: emptySet()
//
//                uiState.pois.forEach { poi ->
//                    // Thêm key() để tăng hiệu suất khi danh sách thay đổi
//                    key(poi.id) {
//                        val poiPoint = poi.location.toPoint()
//                        PointAnnotation(point = poiPoint) {
//                            iconImage = if (pointsOnRoute.contains(poiPoint)) markerOnRoute else markerDefault
//                            iconSize = 0.2
//                            interactionsState.onClicked {
//                                viewModel.onPoiClicked(poi)
//                                true
//                            }
//                        }
//                    }
//                }

                val localContext = LocalContext.current

                val markerDefaultBitmap = remember(localContext) {
                    drawableToBitmap(
                        localContext,
                        R.drawable.charging_station
                    )
                }
                val markerOnRouteBitmap = remember(localContext) {
                    drawableToBitmap(
                        localContext,
                        R.drawable.baseline_location_on_24
                    )
                }
                val annotationManagerRef = remember { mutableStateOf<PointAnnotationManager?>(null) }

                DisposableEffect(Unit) {
                    onDispose {
                        annotationManagerRef.value?.deleteAll()
                    }
                }

                MapEffect(Unit) { mapView ->
                    if (annotationManagerRef.value == null) {
                        annotationManagerRef.value = mapView.annotations.createPointAnnotationManager()
                    }
                }

                LaunchedEffect(annotationManagerRef.value, uiState.pois, route) {
                    val manager = annotationManagerRef.value ?: return@LaunchedEffect

                    val pointsOnRoute = route?.decodedPolyline?.toSet() ?: emptySet()

                    manager.deleteAll()

                    val poiOptions = uiState.pois.map { poi ->
                        val point = poi.location.toPoint()
                        val isOnRoute = pointsOnRoute.contains(point)
                        PointAnnotationOptions()
                            .withPoint(point)
                            .withIconImage(if (isOnRoute) markerOnRouteBitmap else markerDefaultBitmap)
                            .withIconSize(0.08)
                            // Thêm ID vào data để xử lý click
                            .withData(JsonPrimitive(poi.id))
                    }
                    manager.create(poiOptions)

                    manager.addClickListener { annotation ->
                        val poiId = annotation.getData()?.asString
                        if (poiId != null) {
                            val clickedPoi = uiState.pois.find { it.id == poiId }
                            if (clickedPoi != null) {
                                viewModel.onPoiClicked(clickedPoi)
                                return@addClickListener true
                            }
                        }
                        false
                    }
                }


                // Vẽ lộ trình nếu có
                route?.let { aRoute ->
                    PolylineAnnotation(points = aRoute.decodedPolyline) {
                        lineColor = Color.Blue
                        lineWidth = 6.0
                        lineOpacity = 0.8
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

                // Vẽ điểm origin
                uiState.origin?.let { placeWithCoord ->
                    PointAnnotation(point = placeWithCoord.point){
                        iconImage = originIcon
                        iconSize = 0.2
                    }
                }

                // Vẽ điểm destination
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