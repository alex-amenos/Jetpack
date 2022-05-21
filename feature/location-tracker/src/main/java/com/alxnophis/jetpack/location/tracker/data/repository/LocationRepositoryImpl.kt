package com.alxnophis.jetpack.location.tracker.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.location.tracker.data.data.LocationDataSource
import com.alxnophis.jetpack.location.tracker.domain.model.LocationParameters
import com.alxnophis.jetpack.location.tracker.domain.model.LocationState
import kotlinx.coroutines.flow.StateFlow

internal class LocationRepositoryImpl(
    val locationDataSource: LocationDataSource
) : LocationRepository {

    override val locationState: StateFlow<LocationState> =
        locationDataSource.locationState

    override fun provideLastKnownLocation() =
        locationDataSource.provideLastKnownLocation()

    override fun hasLocationAvailable(): Either<Unit, Unit> =
        locationDataSource.hasLocationAvailable()

    override fun hasGPSProviderAvailable(): Either<Unit, Unit> =
        locationDataSource.hasGPSProviderAvailable()

    override suspend fun start(locationParameters: LocationParameters) {
        locationDataSource.start(locationParameters)
    }

    override suspend fun stop() {
        locationDataSource.stop()
    }
}
