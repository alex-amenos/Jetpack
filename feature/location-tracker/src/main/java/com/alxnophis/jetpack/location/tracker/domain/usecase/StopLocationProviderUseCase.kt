package com.alxnophis.jetpack.location.tracker.domain.usecase

import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepository

class StopLocationProviderUseCase(
    private val locationRepository: LocationRepository,
) {
    suspend operator fun invoke() = locationRepository.stopLocationProvider()
}
