package com.alxnophis.jetpack.location.tracker.domain.usecase

import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepository
import com.alxnophis.jetpack.location.tracker.domain.model.LocationState
import kotlinx.coroutines.flow.StateFlow

class LocationStateUseCase(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(): StateFlow<LocationState> = locationRepository.locationState
}
