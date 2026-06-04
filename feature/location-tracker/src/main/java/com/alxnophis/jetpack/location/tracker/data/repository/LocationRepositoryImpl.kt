package com.alxnophis.jetpack.location.tracker.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.location.tracker.data.data.LocationDataSource
import com.alxnophis.jetpack.location.tracker.data.model.Location
import com.alxnophis.jetpack.location.tracker.data.model.LocationParameters
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext

internal class LocationRepositoryImpl(
    private val locationDataSource: LocationDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LocationRepository {
    override val locationSharedFlow: SharedFlow<Location> =
        locationDataSource.locationSharedFlow

    override fun provideLastKnownLocationFlow(): Flow<Location?> = locationDataSource.provideLastKnownLocationFlow()

    override fun hasLocationAvailable(): Either<Unit, Unit> = locationDataSource.hasLocationAvailable()

    override suspend fun startLocationProvider(locationParameters: LocationParameters) {
        withContext(ioDispatcher) {
            locationDataSource.startLocationProvider(locationParameters)
        }
    }

    override suspend fun stopLocationProvider() {
        withContext(ioDispatcher) {
            locationDataSource.stopLocationProvider()
        }
    }
}
