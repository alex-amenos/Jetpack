package com.alxnophis.jetpack.location.tracker.domain.usecase

import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepository

class ProvideLastKnownLocationUseCase(
    private val locationRepository: LocationRepository
) {
    operator fun invoke() = locationRepository.provideLastKnownLocation()
}
