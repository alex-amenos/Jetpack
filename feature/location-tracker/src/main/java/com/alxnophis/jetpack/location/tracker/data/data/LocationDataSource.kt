package com.alxnophis.jetpack.location.tracker.data.data

import arrow.core.Either
import com.alxnophis.jetpack.location.tracker.domain.model.LocationParameters
import com.alxnophis.jetpack.location.tracker.domain.model.LocationState
import kotlinx.coroutines.flow.StateFlow

internal interface LocationDataSource {
    val locationState: StateFlow<LocationState>

    fun provideLastKnownLocation()
    fun hasGPSProviderAvailable(): Either<Unit, Unit>
    fun hasLocationAvailable(): Either<Unit, Unit>
    suspend fun start(locationParameters: LocationParameters)
    suspend fun stop()
}
