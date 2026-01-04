package com.example.frontend

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxDelicateApi
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.BooleanValue
import com.mapbox.maps.extension.compose.style.ColorValue
import com.mapbox.maps.extension.compose.style.layers.ImageValue
import com.mapbox.maps.extension.compose.style.layers.generated.CircleLayer
import com.mapbox.maps.extension.compose.style.layers.generated.LineLayer
import com.mapbox.maps.extension.compose.style.layers.generated.LineWidthUnitValue
import com.mapbox.maps.extension.compose.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.compose.style.sources.GeoJSONData
import com.mapbox.maps.extension.compose.style.sources.generated.rememberGeoJsonSourceState

import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.extension.compose.style.standard.rememberStandardStyleState
import com.mapbox.maps.extension.compose.style.standard.LightPresetValue
import com.mapbox.maps.extension.compose.style.standard.ThemeValue
import com.mapbox.maps.extension.style.expressions.dsl.generated.featureState
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(MapboxDelicateApi::class)
class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    zoom(11.0)
                    center(centerCoordinate)
                    pitch(61.01)
                    bearing(15.77)
                }
            }

            var featureCollection: FeatureCollection? by remember {
                mutableStateOf(null)
            }

            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO){
                    val geoJson = assets.open("ChargePoint.geojson").bufferedReader().use { it.readText() }
                    featureCollection = FeatureCollection.fromJson(geoJson)
                }
            }

            Scaffold (
                floatingActionButton = {
                    FloatingActionButton(
                        modifier = Modifier.padding(16.dp),
                        onClick = {
                            val camera = cameraOptions {
                                center(centerCoordinate)
                                zoom(11.0)
                            }
                            val animationOptions = MapAnimationOptions.Builder()
                                .duration(3000L)
                                .build()
                            mapViewportState.flyTo(camera, animationOptions)
                        },
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(modifier = Modifier.padding(10.dp), text = "Recenter Map")
                    }
                }
            ) {
                paddingValues ->
                MapboxMap(
                    Modifier.fillMaxSize().padding(paddingValues),
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
                    val marker = rememberIconImage(R.drawable.red_marker_official)
                    featureCollection?.features()?.forEach { feature ->
                        val geometry = feature.geometry()
                        if (geometry is Point){
                            val point = geometry
                            PointAnnotation(point = point){
                                iconImage = marker

                                val chargePointName = feature.properties()?.get("name")?.asString
                                val quantity = feature.properties()?.get("quantity")?.asInt

                                interactionsState.onClicked {
                                    val message = "$chargePointName: $quantity connections"
                                    Toast.makeText(
                                        this@TestActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    true
                                }
                            }
                        }
                    }
                }
            }


//            MapboxMap(
//                Modifier.fillMaxSize(),
//                mapViewportState = rememberMapViewportState {
//                    setCameraOptions {
//                        zoom(12.85)
//                        center(Point.fromLngLat(12.64630, 42.50530))
//                        pitch(61.01)
//                        bearing(15.77)
//                    }
//                },
//                style = {
//                    MapboxStandardStyle(
//                        standardStyleState = rememberStandardStyleState {
//                            configurationsState.apply {
//                                lightPreset = LightPresetValue.DAWN
//                                theme = ThemeValue.FADED
//                            }
//                        }
//                    )
//                }
//
//
//            )
//            {
//                val marker = rememberIconImage(R.drawable.red_marker_official)
//                featureCollection?.features()?.forEach { feature ->
//                    val geometry = feature.geometry()
//                    if (geometry is Point){
//                        val point = geometry
//                        PointAnnotation(point = point){
//                            iconImage = marker
//
//                            val chargePointName = feature.properties()?.get("name")?.asString
//                            val quantity = feature.properties()?.get("quantity")?.asInt
//
//                            interactionsState.onClicked {
//                                val message = "$chargePointName: $quantity connections"
//                                Toast.makeText(
//                                    this@TestActivity,
//                                    message,
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                true
//                            }
//                        }
//                    }
//                }
//            }
        }
    }

    private companion object {
        val centerCoordinate = Point.fromLngLat(12.64630, 42.50530)
    }

}