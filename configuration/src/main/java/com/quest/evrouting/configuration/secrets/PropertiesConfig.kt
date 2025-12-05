package com.quest.evrouting.configuration.secrets

import com.quest.evrouting.configuration.secrets.model.Property

object PropertiesConfig {
    val property = Property(
        "secrets.properties",
        "defaults.properties"
    )

    val MAPBOX_API_TOKEN by lazy {
        getProperty("MAPBOX_API_TOKEN")
    }

    val MAPS_API_KEY by lazy {
        getProperty("MAPS_API_KEY")
    }

    fun getProperty(key: String, defaultValue: String = ""): String {
        return property.getProperty(key, defaultValue)
    }

    fun getAllPropertyNames(): Set<String> {
        return property.getAllPropertyNames()
    }
}