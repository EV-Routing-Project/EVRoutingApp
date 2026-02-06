package com.quest.evrouting.phone.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.JsonPrimitive
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapboxDelicateApi
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.standard.LightPresetValue
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.extension.compose.style.standard.ThemeValue
import com.mapbox.maps.extension.compose.style.standard.rememberStandardStyleState
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.quest.evrouting.phone.R
import com.quest.evrouting.phone.configuration.AppConfig
import com.quest.evrouting.phone.domain.model.Place
import com.quest.evrouting.phone.ui.components.SearchTopBar
import com.quest.evrouting.phone.ui.viewmodel.MapViewModel
import com.quest.evrouting.phone.ui.viewmodel.UiEvent
import com.quest.evrouting.phone.util.drawableToBitmap

@OptIn(MapboxDelicateApi::class, ExperimentalMaterial3Api::class)
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
    val cameraTarget by viewModel.cameraTarget.collectAsState()
    val uiEvent = viewModel.uiEvent
    val sheetState = rememberModalBottomSheetState()

    val markerDefaultBitmap = remember(context) { drawableToBitmap(context, R.drawable.charging_station) }
    val markerOnRouteBitmap = remember(context) { drawableToBitmap(context, R.drawable.charging_station2) }

    // State quản lý Annotation Manager
    val annotationManagerRef = remember { mutableStateOf<PointAnnotationManager?>(null) }

    // Bottom Sheet hiển thị POI
    if (uiState.selectedPoi != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onPoiDetailsDismissed() },
            sheetState = sheetState
        ) {
            PoiDetailsSheetContent(poi = uiState.selectedPoi!!)
        }
    }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(11.0)
            center(uiState.centerCoordinate)
            pitch(61.01)
            bearing(15.77)
        }
    }

    // Xử lý Toast Events
    LaunchedEffect(Unit) {
        uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Xử lý tìm đường khi có Origin/Destination mới
    LaunchedEffect(newOrigin, newDestination) {
        if (newOrigin != null && newDestination != null) {
            viewModel.findRouteFromPlaces(newOrigin, newDestination)
            onNewRouteHandled()
        }
    }

    // Xử lý Camera FlyTo
    LaunchedEffect(cameraTarget) {
        cameraTarget?.let { targetPoint ->
            mapViewportState.flyTo(
                cameraOptions = cameraOptions {
                    center(targetPoint)
                    zoom(14.0)
                    padding(EdgeInsets(100.0, 40.0, 100.0, 40.0))
                },
                animationOptions = MapAnimationOptions.mapAnimationOptions { duration(1500L) }
            )
            viewModel.onCameraTargetHandled()
        }
    }

    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                if (newOrigin != null && newDestination != null) {
                    FloatingActionButton(
                        onClick = { viewModel.findRouteFromPlaces(newOrigin, newDestination) },
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
                    }
                    Spacer(Modifier.height(16.dp))
                }

                FloatingActionButton(
                    onClick = {
                        val (cameraOptions, animationOptions) = viewModel.onRecenterMapClicked()
                        mapViewportState.flyTo(cameraOptions, animationOptions)
                    },
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
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
                MapEffect(Unit) { mapView ->
                    if (annotationManagerRef.value == null) {
                        annotationManagerRef.value = mapView.annotations.createPointAnnotationManager()
                    }
                }

                val hasListener = remember { mutableStateOf(false) }

                LaunchedEffect(uiState.pois, uiState.recommendedPoiIds, annotationManagerRef.value, route) {
                    val manager = annotationManagerRef.value ?: return@LaunchedEffect
                    manager.deleteAll()

                    val groupedByPoint = uiState.pois.groupBy { it.location.toPoint() }

                    val poiOptions = groupedByPoint.map { (point, poisAtSameLocation) ->
                        val hasRecommended = poisAtSameLocation.any {
                            uiState.recommendedPoiIds?.contains(it.id) == true
                        }

                        PointAnnotationOptions()
                            .withPoint(point)
                            .withIconImage(
                                if (hasRecommended) markerOnRouteBitmap
                                else markerDefaultBitmap
                            )
                            .withIconSize(0.08)
                            // lưu toàn bộ ids tại vị trí này
                            .withData(
                                JsonPrimitive(
                                    poisAtSameLocation.joinToString(",") { it.id }
                                )
                            )
                    }

                    if (poiOptions.isNotEmpty()) {
                        manager.create(poiOptions)
                    }

                    if (!hasListener.value) {
                        manager.addClickListener { annotation ->
                            val ids = annotation.getData()?.asString?.split(",") ?: return@addClickListener false
                            val firstPoi = uiState.pois.firstOrNull { it.id == ids.first() }
                            if (firstPoi != null) {
                                viewModel.onPoiClicked(firstPoi)
                                true
                            } else false
                        }

                        hasListener.value = true
                        Log.d("MARKER_DEBUG", "Added click listener to manager")
                    }
                }

                route?.let { path ->
                    PolylineAnnotation(points = path.decodedPolyline) {
                        lineColor = Color(0xFF2196F3)
                        lineWidth = 6.0
                        lineOpacity = 0.8
                        lineJoin = LineJoin.ROUND
                    }
                }

                val carIcon = rememberIconImage(R.drawable.car2)
                val originIcon = rememberIconImage(R.drawable.user_location)
                val destinationIcon = rememberIconImage(R.drawable.pin)

                if (tripState.isActive) {
                    PointAnnotation(point = tripState.currentLocation) {
                        iconImage = carIcon
                        iconSize = 0.2
                    }
                }

                route?.decodedPolyline?.let { points ->
                    if (points.isNotEmpty()) {
                        PointAnnotation(point = points.first()) {
                            iconImage = originIcon
                            iconSize = 0.2
                        }
                        PointAnnotation(point = points.last()) {
                            iconImage = destinationIcon
                            iconSize = 0.2
                        }
                    }
                }
            }

            SearchTopBar(
                onSearchClick = onSearchClick,
                modifier = Modifier.align(Alignment.TopCenter).padding(16.dp)
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            uiState.errorMessage?.let { message ->
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = message, color = Color.White, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Composable
fun PoiDetailsSheetContent(poi: com.quest.evrouting.phone.domain.model.POI) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        item {
            Text("Thông Tin Trạm Sạc", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text("Trạng thái: ${poi.status}", fontSize = 16.sp)
            poi.information["address"]?.let { Text("Địa chỉ: $it", fontSize = 16.sp) }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text("Các Cổng Sạc", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (poi.connectors.isEmpty()) {
            item { Text("- Không có thông tin cổng sạc.") }
        } else {
            items(poi.connectors) { connector ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text("• Loại: ${connector.connectorType}")
                    Text("  Công suất: ${connector.maxElectricPower} W")
                }
            }
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}