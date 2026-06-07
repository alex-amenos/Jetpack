package com.alxnophis.jetpack.location.tracker.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.location.tracker.data.data.LocationDataSource
import com.alxnophis.jetpack.location.tracker.data.model.Location
import com.alxnophis.jetpack.location.tracker.data.model.LocationParameters
import kotlinx.coroutines.flow.Flow

internal class LocationRepositoryImpl(
    private val locationDataSource: LocationDataSource,
) : LocationRepository {
    override fun getLocationFlow(parameters: LocationParameters): Flow<Location> = locationDataSource.getLocationFlow(parameters)

    override fun provideLastKnownLocationFlow(): Flow<Location?> = locationDataSource.provideLastKnownLocationFlow()

    override fun hasLocationAvailable(): Either<Unit, Unit> = locationDataSource.hasLocationAvailable()
}
