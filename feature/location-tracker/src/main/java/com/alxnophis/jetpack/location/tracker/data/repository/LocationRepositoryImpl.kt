package com.alxnophis.jetpack.location.tracker.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.location.tracker.data.data.LocationDataSource
import com.alxnophis.jetpack.location.tracker.domain.model.Location
import com.alxnophis.jetpack.location.tracker.domain.model.LocationParameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

internal class LocationRepositoryImpl(
    val locationDataSource: LocationDataSource
) : LocationRepository {

    override val locationSharedFlow: SharedFlow<Location> =
        locationDataSource.locationSharedFlow

    override fun provideLastKnownLocationFlow(): Flow<Location?> =
        locationDataSource.provideLastKnownLocationFlow()

    override fun hasLocationAvailable(): Either<Unit, Unit> =
        locationDataSource.hasLocationAvailable()

    override suspend fun startLocationProvider(locationParameters: LocationParameters) {
        locationDataSource.startLocationProvider(locationParameters)
    }

    override suspend fun stopLocationProvider() {
        locationDataSource.stopLocationProvider()
    }
}
