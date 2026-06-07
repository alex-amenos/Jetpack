package com.alxnophis.jetpack.location.tracker.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.location.tracker.data.model.Location
import com.alxnophis.jetpack.location.tracker.data.model.LocationParameters
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getLocationFlow(parameters: LocationParameters): Flow<Location>

    fun provideLastKnownLocationFlow(): Flow<Location?>

    fun hasLocationAvailable(): Either<Unit, Unit>
}
