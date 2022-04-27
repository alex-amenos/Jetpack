package com.alxnophis.jetpack.location.tracker.domain.usecase

import com.alxnophis.jetpack.location.tracker.data.repository.LastKnownLocationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StopLastKnownLocationRequestUseCase(
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val lastKnownLocationRepository: LastKnownLocationRepository
) {
    suspend operator fun invoke() =
        withContext(dispatcherIO) {
            lastKnownLocationRepository.stopLocationRequest()
        }
}