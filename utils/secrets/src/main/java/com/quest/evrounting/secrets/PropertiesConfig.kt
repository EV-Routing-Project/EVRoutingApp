package com.quest.evrounting.secrets

object PropertiesConfig {
    private val property = Property(
        "secrets.properties",
        "default.properties")

    fun getProperty(key: String, defaultValue: String? = null): String? {
        return property.getProperty(key, defaultValue)
    }

    fun getAllPropertyNames(): Set<String> {
        return property.getAllPropertyNames()
    }
}