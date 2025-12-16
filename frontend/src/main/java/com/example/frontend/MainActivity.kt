package com.example.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxDelicateApi
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

@OptIn(MapboxDelicateApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val context = LocalContext.current
            var geoJsonString by remember { mutableStateOf("") }

            // Read GeoJSON from assets
            LaunchedEffect(Unit) {
                geoJsonString = context.assets.open("ChargePoint.geojson").bufferedReader().use { it.readText() }
            }

            val geoJsonSource = rememberGeoJsonSourceState(sourceId = "my-geojson-source")

            // Set data when geoJsonString is loaded
            if (geoJsonString.isNotEmpty()) {
                geoJsonSource.data = GeoJSONData(geoJsonString)
            }


            MapboxMap(
                Modifier.fillMaxSize(),
                mapViewportState = rememberMapViewportState {
                    setCameraOptions {
                        zoom(12.85)
                        center(Point.fromLngLat(12.64630, 42.50530))
                        pitch(61.01)
                        bearing(15.77)
                    }
                },
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


            )
            {
                val marker = rememberIconImage(R.drawable.red_marker_official)
                PointAnnotation(point = Point.fromLngLat(12.64630, 42.50530))
                {
                    iconImage = marker
                }

                CircleLayer(
                    sourceState = geoJsonSource,
                    layerId = "my-circle-layer",
                ) {
                    circleColor = ColorValue(Color(0xffff0000))
                }

                LineLayer(
                    sourceState = geoJsonSource,
                    layerId = "my-line-layer",
                ) {

                }


            }
        }
    }
}