package com.alxnophis.jetpack.location.tracker.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.location.tracker.domain.model.Location
import com.alxnophis.jetpack.location.tracker.domain.model.LocationParameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface LocationRepository {
    val locationSharedFlow: SharedFlow<Location>

    fun provideLastKnownLocationFlow(): Flow<Location?>

    fun hasLocationAvailable(): Either<Unit, Unit>

    suspend fun startLocationProvider(locationParameters: LocationParameters)

    suspend fun stopLocationProvider()
}
