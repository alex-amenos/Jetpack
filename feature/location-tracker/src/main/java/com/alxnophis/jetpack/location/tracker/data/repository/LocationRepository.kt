package com.alxnophis.jetpack.location.tracker.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.location.tracker.domain.model.LocationParameters
import com.alxnophis.jetpack.location.tracker.domain.model.LocationState
import kotlinx.coroutines.flow.StateFlow

interface LocationRepository {
    val locationState: StateFlow<LocationState>

    fun provideLastKnownLocation()
    fun hasGPSProviderAvailable(): Either<Unit, Unit>
    fun hasLocationAvailable(): Either<Unit, Unit>
    suspend fun start(locationParameters: LocationParameters)
    suspend fun stop()
}
