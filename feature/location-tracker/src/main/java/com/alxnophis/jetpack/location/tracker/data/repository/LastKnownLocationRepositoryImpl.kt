package com.alxnophis.jetpack.location.tracker.data.repository

import com.alxnophis.jetpack.location.tracker.data.data.FusedLocationDataSource
import com.alxnophis.jetpack.location.tracker.domain.model.Location
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

internal class LastKnownLocationRepositoryImpl(
    dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val fusedLocationDataSource: FusedLocationDataSource
) : LastKnownLocationRepository {

    private var job: Job? = null
    private val scope: CoroutineScope = CoroutineScope(dispatcherIO + SupervisorJob())
    private val _locationStateFlow: MutableStateFlow<Location?> = MutableStateFlow(null)

    override val locationStateFlow: StateFlow<Location?> = _locationStateFlow.asStateFlow()

    override suspend fun startLocationRequest(locationIntervalMillis: Long) {
        if (job == null) {
            job = scope.async {
                while (true) {
                    locationRequest()
                    delay(locationIntervalMillis)
                }
            }
        }
    }

    override suspend fun stopLocationRequest() {
        job?.cancelAndJoin()
        job = null
    }

    /**
     * One time location request
     */
    private fun locationRequest() {
        fusedLocationDataSource.getLastKnownLocation { location: android.location.Location? ->
            location?.let {
                saveLocation(
                    Location(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        altitude = location.altitude,
                        accuracy = location.accuracy,
                        speed = location.speed,
                        bearing = location.bearing,
                        time = location.time
                    )
                )
            }
        }
    }

    private fun saveLocation(location: Location) {
        Timber.d("New location saved on repo $location")
        _locationStateFlow.update { location }
    }
}
