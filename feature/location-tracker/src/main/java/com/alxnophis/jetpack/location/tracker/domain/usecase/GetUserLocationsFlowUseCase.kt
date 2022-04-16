package com.alxnophis.jetpack.location.tracker.domain.usecase

import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepository
import com.alxnophis.jetpack.location.tracker.domain.model.Location
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class GetUserLocationsFlowUseCase(
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): StateFlow<Location?> =
        withContext(dispatcherIO) {
            locationRepository.locationStateFlow
        }
}
