package com.quest.evrouting.phone.data.remote.api.backendServer.directions

import android.content.Context
import com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.request.DirectionsRequest
import com.quest.evrouting.phone.data.remote.api.backendServer.directions.dto.response.DirectionsResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class MockDirectionsApiService(private val context: Context) : DirectionsApiService {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun getDirections(requestBody: DirectionsRequest): DirectionsResponseDto {
        return withContext(Dispatchers.IO) {
            val jsonString = context.assets.open("directions_ev.json")
                .bufferedReader()
                .use { it.readText() }
            json.decodeFromString<DirectionsResponseDto>(jsonString)
        }
    }
}
