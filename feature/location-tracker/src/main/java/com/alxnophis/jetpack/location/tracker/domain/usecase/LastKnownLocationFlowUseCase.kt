package com.alxnophis.jetpack.location.tracker.domain.usecase

import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepository
import com.alxnophis.jetpack.location.tracker.domain.model.Location
import kotlinx.coroutines.flow.Flow

class LastKnownLocationFlowUseCase(
    private val locationRepository: LocationRepository,
) {
    operator fun invoke(): Flow<Location?> = locationRepository.provideLastKnownLocationFlow()
}
