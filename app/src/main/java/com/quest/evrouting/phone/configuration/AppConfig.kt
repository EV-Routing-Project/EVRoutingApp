package com.quest.evrouting.phone.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.quest.evrouting.phone.data.repository.ChargePointRepositoryImpl
import com.quest.evrouting.phone.data.repository.EVRouteRepositoryImpl
import com.quest.evrouting.phone.data.repository.GeocodingRepositoryImpl
import com.quest.evrouting.phone.domain.repository.ChargePointRepository
import com.quest.evrouting.phone.domain.repository.EVRouteRepository
import com.quest.evrouting.phone.domain.repository.GeocodingRepository
import com.quest.evrouting.phone.domain.usecase.*
import com.quest.evrouting.phone.ui.viewmodel.MapViewModel

/**
 * Singleton object chịu trách nhiệm khởi tạo và cung cấp các phụ thuộc
 * cho toàn bộ module `app`. Đây là cách làm Dependency Injection thủ công.
 */
object AppConfig {

    // -> 2. XÓA BỎ HOÀN TOÀN KHỐI KHỞI TẠO RETROFIT VÀ API SERVICE
    // Các đối tượng này giờ sẽ được lấy từ BackendApiClient

    // 1. Khởi tạo các Repository
    private val chargePointRepository: ChargePointRepository by lazy {
        ChargePointRepositoryImpl()
    }

    private val evRouteRepository: EVRouteRepository by lazy {
        EVRouteRepositoryImpl()
    }

    private val geocodingRepository: GeocodingRepository by lazy {
        GeocodingRepositoryImpl()
    }

    // 2. Khởi tạo UseCase (phụ thuộc vào Repository)
    private val getChargePointsUseCase: GetChargePointsUseCase by lazy {
        GetChargePointsUseCase(chargePointRepository)
    }

    private val simulateTripUseCase: SimulateTripUseCase by lazy {
        SimulateTripUseCaseImpl(
            evRouteRepository = evRouteRepository,
            chargePointRepository = chargePointRepository
        )
    }

    // 3. Tạo một ViewModelFactory để "dạy" Android cách tạo MapViewModel
    val mapViewModelFactory: ViewModelProvider.Factory by lazy {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return MapViewModel(
                        getChargePointsUseCase = getChargePointsUseCase,
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
