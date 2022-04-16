package com.alxnophis.jetpack.location.tracker.data.repository

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.alxnophis.jetpack.location.tracker.domain.model.Location
import com.google.android.gms.location.LocationServices
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

internal class LocationRepositoryImpl(
    dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val context: Context,
) : LocationRepository {

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
        if (isGPSEnabled() && (checkFineLocationPermission() || checkCoarseLocationPermission())) {
            LocationServices
                .getFusedLocationProviderClient(context)
                .lastLocation
                .addOnSuccessListener { location: android.location.Location? ->
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
    }

    private fun saveLocation(location: Location) {
        Timber.d("New location saved on repo $location")
        _locationStateFlow.update { location }
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun checkFineLocationPermission(): Boolean = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED

    private fun checkCoarseLocationPermission(): Boolean = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
}
