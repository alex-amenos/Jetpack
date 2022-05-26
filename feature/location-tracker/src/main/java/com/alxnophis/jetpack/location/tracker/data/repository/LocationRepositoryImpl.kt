package com.alxnophis.jetpack.location.tracker.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.location.tracker.data.data.LocationDataSource
import com.alxnophis.jetpack.location.tracker.domain.model.Location
import com.alxnophis.jetpack.location.tracker.domain.model.LocationParameters
import kotlinx.coroutines.flow.SharedFlow

internal class LocationRepositoryImpl(
    val locationDataSource: LocationDataSource
) : LocationRepository {

    override val locationSharedFlow: SharedFlow<Location> =
        locationDataSource.locationSharedFlow

    override val lastKnownLocationSharedFlow: SharedFlow<Location>
        get() = locationDataSource.lastKnownLocationSharedFlow

    override fun provideLastKnownLocation() =
        locationDataSource.provideLastKnownLocation()

    override fun hasLocationAvailable(): Either<Unit, Unit> =
        locationDataSource.hasLocationAvailable()

    override suspend fun start(locationParameters: LocationParameters) {
        locationDataSource.start(locationParameters)
    }

    override suspend fun stop() {
        locationDataSource.stop()
    }
}
