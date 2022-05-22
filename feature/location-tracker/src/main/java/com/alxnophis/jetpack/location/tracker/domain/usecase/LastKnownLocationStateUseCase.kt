package com.alxnophis.jetpack.location.tracker.domain.usecase

import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepository
import com.alxnophis.jetpack.location.tracker.domain.model.LastKnownLocationState
import kotlinx.coroutines.flow.StateFlow

class LastKnownLocationStateUseCase(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(): StateFlow<LastKnownLocationState> = locationRepository.lastKnownLocationState
}
