package com.alxnophis.jetpack.location.tracker.domain.usecase

import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StartLocationRequestUseCase(
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(locationIntervalMillis: Long) =
        withContext(dispatcherIO) {
            locationRepository.startLocationRequest(locationIntervalMillis)
        }
}
