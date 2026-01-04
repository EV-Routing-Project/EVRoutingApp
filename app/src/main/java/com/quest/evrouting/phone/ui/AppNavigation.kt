package com.quest.evrouting.phone.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.quest.evrouting.phone.domain.model.Place
import com.quest.evrouting.phone.ui.screens.MapScreen
import com.quest.evrouting.phone.ui.screens.SearchScreen

// Định nghĩa các route cho các màn hình
object AppDestinations {
    const val MAP_ROUTE = "map"
    const val SEARCH_ROUTE = "search"

    const val RESULT_ORIGIN_PLACE = "result_origin_place"
    const val RESULT_DESTINATION_PLACE = "result_destination_place"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestinations.MAP_ROUTE
    ) {
        composable(AppDestinations.MAP_ROUTE) { navBackStackEntry ->
            // Nhận kết quả trả về từ SearchScreen
            val savedStateHandle = navBackStackEntry.savedStateHandle
            val origin: Place? = savedStateHandle.get<Place>(AppDestinations.RESULT_ORIGIN_PLACE)
            val destination: Place? = savedStateHandle.get<Place>(AppDestinations.RESULT_DESTINATION_PLACE)


            MapScreen(
                // Truyền hàm điều hướng vào MapScreen
                onSearchClick = {
                    navController.navigate(AppDestinations.SEARCH_ROUTE)
                },
                // Truyền dữ liệu mới nhận được vào MapScreen
                newOrigin = origin,
                newDestination = destination,
                // Hàm để xóa dữ liệu sau khi đã sử dụng
                onNewRouteHandled = {
                    savedStateHandle.remove<Place>(AppDestinations.RESULT_ORIGIN_PLACE)
                    savedStateHandle.remove<Place>(AppDestinations.RESULT_DESTINATION_PLACE)
                }
            )
        }
        composable(AppDestinations.SEARCH_ROUTE) {
            SearchScreen(
                // Truyền hàm để quay lại MapScreen VÀ gửi dữ liệu
                onNavigateBackWithResult = { origin, destination ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(AppDestinations.RESULT_ORIGIN_PLACE, origin)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(AppDestinations.RESULT_DESTINATION_PLACE, destination)
                    navController.popBackStack()
                },
                // Truyền hàm để quay lại bình thường
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
