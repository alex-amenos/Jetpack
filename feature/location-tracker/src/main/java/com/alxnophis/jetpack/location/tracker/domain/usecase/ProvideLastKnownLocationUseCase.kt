package com.alxnophis.jetpack.location.tracker.domain.usecase

import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProvideLastKnownLocationUseCase(
    private val iODispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke() = withContext(iODispatcher) {
        locationRepository.provideLastKnownLocationFlow()
    }
}
