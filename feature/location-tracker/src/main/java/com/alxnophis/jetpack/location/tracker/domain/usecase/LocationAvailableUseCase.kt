package com.alxnophis.jetpack.location.tracker.domain.usecase

import arrow.core.Either
import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepository

class LocationAvailableUseCase(
    private val locationRepository: LocationRepository,
) {
    operator fun invoke(): Either<Unit, Unit> = locationRepository.hasLocationAvailable()
}
