package com.quest.evrouting.algorithm.infrastructure.adapter.tool.routing

import com.quest.evrouting.algorithm.domain.model.Point
import com.quest.evrouting.algorithm.domain.port.model.PathPort
import com.quest.evrouting.algorithm.domain.port.tool.routing.RoutingServicePort
import com.quest.evrouting.algorithm.infrastructure.adapter.tool.geometry.GeometryProviderAdapter
import com.quest.evrouting.configuration.secrets.PropertiesConfig
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.system.measureTimeMillis

@RunWith(Parameterized::class)
class RoutingServiceAdapterTest(
    private val testcaseName: String,
    private val start: Point,
    private val end: Point
) {
    private lateinit var routingService: RoutingServicePort
    @Before
    fun setUp() {
        routingService = RoutingServiceAdapter(
            RoutingProviderAdapter(PropertiesConfig.getProperty("MAPBOX_API_TOKEN")),
            GeometryProviderAdapter()
        )
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun parameters(): Collection<Array<Any>> {
            return listOf(
                arrayOf(
                    "Test case 0: Set up",
                    Point(0.0,0.0),
                    Point(0.0,0.0)
                ),
                arrayOf(
                    "Test case 1: Hà Nội",
                    Point(105.8342, 21.0278), // Điểm bắt đầu
                    Point(105.8525, 21.0288)  // Điểm kết thúc (Hồ Gươm)
                ),
                arrayOf(
                    "Test case 2: TP.HCM",
                    Point(106.70175, 10.77602), // Quận 1
                    Point(106.65739, 10.81854)  // Sân bay TSN
                ),
                arrayOf(
                    "Test case 3: Đà Nẵng -> Hội An",
                    Point(108.2208,16.0544), // Đà Nẵng
                    Point(108.3380, 15.8801)  // Hội An
                ),
                arrayOf(
                    "Test case 4: San Francisco -> Los Angeles (USA)",
                    Point(-122.4194, 37.7749), // San Francisco
                    Point(-118.2437, 34.0522)  // Los Angeles
                ),
                arrayOf(
                    "Test case 5: Paris -> Versailles (Pháp)",
                    Point(2.3522, 48.8566),   // Trung tâm Paris
                    Point(2.1301, 48.8049)    // Cung điện Versailles
                ),
                arrayOf(
                    "Test case 6: Tokyo -> Kyoto (Nhật Bản)",
                    Point(139.6917, 35.6895),  // Tokyo
                    Point(135.7681, 35.0116)   // Kyoto
                ),
                arrayOf(
                    "Test case 7: Sydney Opera House -> Bondi Beach (Úc)",
                    Point(151.2153, -33.8568), // Sydney Opera House
                    Point(151.2777, -33.8915)  // Bondi Beach
                ),
                arrayOf(
                    "Test case 8: London Eye -> Tower Bridge (Anh)",
                    Point(-0.1195, 51.5033),  // London Eye
                    Point(-0.0754, 51.5055)   // Tower Bridge
                ),
                arrayOf(
                    "Test case 9: Cairo -> Giza Pyramids (Ai Cập)",
                    Point(31.2357, 30.0444),   // Trung tâm Cairo
                    Point(31.1342, 29.9792)    // Kim tự tháp Giza
                ),
                arrayOf(
                    "Test case 10: Rio de Janeiro -> Tượng Chúa Cứu Thế (Brazil)",
                    Point(-43.1729, -22.9068), // Trung tâm Rio
                    Point(-43.2105, -22.9519)  // Tượng Chúa Cứu Thế
                ),
            )
        }
    }

    @Test
    fun testGetHaversineDistance() {
        println("--- Đang chạy test case: $testcaseName")
        var distance = 0.0
        val excureTime = measureTimeMillis {
            distance = routingService.getHaversineDistance(start, end)
        }
        Assert.assertTrue("Khoảng cách đường đi không hợp lệ", distance >= 0)
        println("Khoảng cách giữa hai điểm: $distance")
        println("Thời gian chạy: $excureTime ms")
        println("Test thành công!\n")
    }

    @Test
    fun testFindRoute() = runBlocking {
        println("--- [$testcaseName] Đang chạy testFindRoute ---")
        var routeResult: PathPort?
        val excureTime = measureTimeMillis {
            routeResult = routingService.findShortestPath(start, end)
        }
        Assert.assertNotNull("Kết quả tìm đường không được null", routeResult)
        println("distance: ${routeResult?.toLineString()?.distance}")
        println("Thời gian chạy: $excureTime ms")
        println("Test thành công!\n")
    }
}