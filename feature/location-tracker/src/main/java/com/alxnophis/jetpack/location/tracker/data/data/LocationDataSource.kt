package com.alxnophis.jetpack.location.tracker.data.data

import arrow.core.Either
import com.alxnophis.jetpack.location.tracker.domain.model.Location
import com.alxnophis.jetpack.location.tracker.domain.model.LocationParameters
import kotlinx.coroutines.flow.SharedFlow

internal interface LocationDataSource {
    val locationSharedFlow: SharedFlow<Location>
    val lastKnownLocationSharedFlow: SharedFlow<Location>

    fun provideLastKnownLocation()
    fun hasLocationAvailable(): Either<Unit, Unit>
    suspend fun start(locationParameters: LocationParameters)
    suspend fun stop()
}
