package com.quest.evrouting.algorithm.config.utils

import com.quest.evrouting.algorithm.domain.model.Point
import com.quest.evrouting.algorithm.domain.port.tool.routing.RoutingProviderPort
import com.quest.evrouting.algorithm.infrastructure.adapter.tool.routing.RoutingProviderAdapter
import com.quest.evrouting.configuration.secrets.PropertiesConfig
import kotlinx.coroutines.runBlocking

class Test {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = runBlocking {
            val routingProvider: RoutingProviderPort = RoutingProviderAdapter(PropertiesConfig.getProperty("MAPBOX_API_TOKEN"))
            val path = routingProvider.findRoute(Point(12.0,12.0), Point(12.4,12.0), RoutingProfile.DRIVING)
            println(path?.distance ?: 0.0)
            println(PropertiesConfig.property.findProjectRoot())
        }
    }
}
