package com.quest.evrouting.phone.configuration

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.quest.evrouting.phone.data.repository.EVRouteRepositoryImpl
import com.quest.evrouting.phone.data.repository.GeocodingRepositoryImpl
import com.quest.evrouting.phone.data.repository.PoiRepositoryImpl
import com.quest.evrouting.phone.domain.repository.EVRouteRepository
import com.quest.evrouting.phone.domain.repository.GeocodingRepository
import com.quest.evrouting.phone.domain.repository.PoiRepository
import com.quest.evrouting.phone.domain.usecase.*
import com.quest.evrouting.phone.ui.viewmodel.MapViewModel

object AppConfig {
    private lateinit var poiRepository: PoiRepository
    private lateinit var evRouteRepository: EVRouteRepository
    private lateinit var geocodingRepository: GeocodingRepository

    private val getPoisUseCase: GetPoisUseCase by lazy {
        GetPoisUseCase(poiRepository)
    }
    private val simulateTripUseCase: SimulateTripUseCase by lazy {
        SimulateTripUseCaseImpl(
            evRouteRepository = evRouteRepository,
            poiRepository = poiRepository
        )
    }

    fun initialize(context: Context) {
        val appContext = context.applicationContext

        poiRepository = PoiRepositoryImpl()
        evRouteRepository = EVRouteRepositoryImpl(appContext)
        geocodingRepository = GeocodingRepositoryImpl()
    }

    val mapViewModelFactory: ViewModelProvider.Factory by lazy {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return MapViewModel(
                        getPoisUseCase = getPoisUseCase,
                        evRouteRepository = evRouteRepository,
                        geocodingRepository = geocodingRepository,
                        simulateTripUseCase = simulateTripUseCase,
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}

