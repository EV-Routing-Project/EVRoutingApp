package com.quest.evrouting.configuration.utils.secrets

import com.quest.evrouting.secrets.domain.PropertiesServicePort
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class PropertiesConfigTest(
    private val testcaseName: String,
    private val keyName: String,
) {
    companion object {
        val propertiesService: PropertiesServicePort = SecretsConfig.propertiesService
        @JvmStatic
        @BeforeClass
        fun setUp() {
            println("Before Class")
            println("MAPBOX_API_KEY: ${propertiesService.getMapboxApiToken()}")
            println("MAPS_API_KEY: ${propertiesService.getMapsApiKey()}")
            println("Done\n")
        }

        @JvmStatic
        @Parameterized.Parameters
        fun parameters(): Collection<Array<Any>>{
            val list = mutableListOf<Array<Any>>()
            list.add(arrayOf("Testcase 0", "ANY_KEY"))
            list.add(arrayOf("Testcase 1", "MAPBOX_API_TOKEN"))
            list.add(arrayOf("Testcase 2", "MAPS_API_KEY"))
            return list
        }
    }

    @Test
    fun testGetProperties(){
        val result = propertiesService.getProperty(keyName)
        println("--- [$testcaseName] Đang chạy testGetProperties ---")
        println("- key: $keyName")
        println("- value: $result")
        println("Done Test!\n")
    }
}