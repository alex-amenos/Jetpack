package com.alxnophis.jetpack.location.tracker.domain.usecase

import arrow.core.Either
import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepository

class GPSProviderAvailableUseCase(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(): Either<Unit, Unit> = locationRepository.hasGPSProviderAvailable()
}
