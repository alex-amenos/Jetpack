package com.alxnophis.jetpack.location.tracker.domain.usecase

import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepository
import kotlinx.coroutines.withContext

class ProvideLastKnownLocationUseCase(
    private val dispatchers: DispatcherProvider,
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke() = withContext(dispatchers.io) {
        locationRepository.provideLastKnownLocationFlow()
    }
}
