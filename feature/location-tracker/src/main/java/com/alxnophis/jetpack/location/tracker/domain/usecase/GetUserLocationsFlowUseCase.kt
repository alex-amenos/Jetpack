package com.alxnophis.jetpack.location.tracker.domain.usecase

import com.alxnophis.jetpack.location.tracker.data.repository.LastKnownLocationRepository
import com.alxnophis.jetpack.location.tracker.domain.model.Location
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class GetUserLocationsFlowUseCase(
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val lastKnownLocationRepository: LastKnownLocationRepository
) {
    suspend operator fun invoke(): StateFlow<Location?> =
        withContext(dispatcherIO) {
            lastKnownLocationRepository.locationStateFlow
        }
}
