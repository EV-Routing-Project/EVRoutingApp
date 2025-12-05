package com.quest.evrouting.secrets.domain

interface PropertiesServicePort {
    fun getProperty(key: String, defaultValue: String = "") : String
    fun getAllPropertyNames(): Set<String>
    fun getMapboxApiToken(): String
    fun getMapsApiKey(): String
}