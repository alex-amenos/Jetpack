package com.alxnophis.jetpack.location.tracker.domain.usecase

import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepository
import com.alxnophis.jetpack.location.tracker.domain.model.LocationParameters

class StartLocationProviderUseCase(
    private val locationRepository: LocationRepository,
) {
    suspend operator fun invoke(locationParameters: LocationParameters = LocationParameters()) =
        locationRepository.startLocationProvider(locationParameters)
}
