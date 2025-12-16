import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

//@OptIn(MapboxExperimental::class)
//@Composable
//fun MapScreen(
//    chargePoints: List<ChargePoint>,
//    mapViewModel: MapViewModel = viewModel() // 1. Lấy ViewModel
//) {
//    // ... code thiết lập camera và mapViewportState như cũ
//
//    // 2. Lắng nghe trạng thái đường đi từ ViewModel
//    val routeLineString by mapViewModel.route.collectAsState()
//
//    // 3. Tìm đường đi khi màn hình được khởi tạo
//    LaunchedEffect(Unit) {
//        if (chargePoints.size >= 2) {
//            val origin = Point.fromLngLat(chargePoints[0].longitude, chargePoints[0].latitude)
//            val destination = Point.fromLngLat(chargePoints[1].longitude, chargePoints[1].latitude)
//            mapViewModel.findDirections(origin, destination)
//        }
//    }
//
//    // 4. Tạo nguồn dữ liệu và layer cho đường đi
//    val routeSource = remember(routeLineString) {
//        val sourceBuilder = GeoJsonSource.Builder("route-source")
//        routeLineString?.let {
//            sourceBuilder.geometry(it)
//        }
//        sourceBuilder.build()
//    }
//
//    val routeLayer = remember {
//        LineLayer("route-layer", "route-source").apply {
//            lineColor(android.graphics.Color.parseColor("#4882c5"))
//            lineWidth(8.0)
//            lineOpacity(0.8)
//        }
//    }
//
//    // ... code của GeoJsonSource và CircleLayer cho các POI như cũ...
//
//    MapboxMap(
//        modifier = Modifier.fillMaxSize(),
//        mapViewportState = mapViewportState,
//        style = {
//            MapboxStyle(
//                style = Style.MAPBOX_STREETS,
//                // 5. Thêm nguồn và layer của đường đi vào bản đồ
//                // Đặt layer của đường đi bên dưới layer của điểm POI
//                sources = listOf(poiGeoJsonSource, routeSource),
//                layers = listOf(routeLayer, poiCircleLayer)
//            ) {
//                // Cập nhật lại source khi có dữ liệu đường đi mới
//                if (it.styleState == SourceState.INITIALIZED) {
//                    it.updateGeoJsonSource("route-source", routeLineString)
//                }
//            }
//        }
//    )
//}


import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap

@OptIn(MapboxExperimental::class)
@Composable
fun MapScreen() {
    // Chỉ cần gọi Composable này là bản đồ sẽ được hiển thị
    MapboxMap(
        modifier = Modifier.fillMaxSize()
    )
}

    