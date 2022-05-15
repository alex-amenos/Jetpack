package com.alxnophis.jetpack.location.tracker.domain.usecase

import com.alxnophis.jetpack.location.tracker.data.repository.LastKnownLocationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StartLastKnownLocationRequestUseCase(
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val lastKnownLocationRepository: LastKnownLocationRepository
) {
    suspend operator fun invoke(locationIntervalMillis: Long) =
        withContext(dispatcherIO) {
            lastKnownLocationRepository.startLocationRequest(locationIntervalMillis)
        }
}
