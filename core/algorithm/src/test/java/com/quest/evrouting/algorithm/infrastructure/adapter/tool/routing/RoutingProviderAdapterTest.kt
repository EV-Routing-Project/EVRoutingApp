package com.quest.evrouting.algorithm.infrastructure.adapter.tool.routing

import com.quest.evrouting.algorithm.config.utils.RoutingProfile
import com.quest.evrouting.algorithm.domain.model.Point
import com.quest.evrouting.algorithm.domain.port.tool.routing.RoutingProviderPort
import com.quest.evrouting.configuration.secrets.PropertiesConfig
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert
class RoutingProviderAdapterTest {
    private lateinit var routingProvider: RoutingProviderPort

    @Before
    fun setUp() {
        routingProvider = RoutingProviderAdapter(PropertiesConfig.getProperty("MAPBOX_API_TOKEN"))
    }

    @Test
    fun testFindRoute() = runBlocking {
        val origin = Point(105.8342, 21.0278)
        val destination = Point(106.6297, 10.8231)
        val profile = RoutingProfile.DRIVING
        val response = routingProvider.findRoute(origin, destination, profile)
        Assert.assertNotNull("Không tìm thấy đường đi", response)
        Assert.assertTrue("Khoảng cách đường đi không hợp lệ", (response?.distance ?: 0.0) > 0.0)
        println("Khoảng cách đường đi: ${response?.distance}")
        println("Test thành công!")
    }
}