package com.alxnophis.jetpack.location.tracker.data.data

import android.annotation.SuppressLint
import android.location.LocationManager
import android.os.Looper
import androidx.core.location.LocationManagerCompat
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import com.alxnophis.jetpack.location.tracker.domain.model.Location
import com.alxnophis.jetpack.location.tracker.domain.model.LocationParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import android.location.Location as AndroidLocation

internal class LocationDataSourceImpl(
    dispatcherProvider: DispatcherProvider,
    private val fusedLocationProvider: FusedLocationProviderClient,
    private val locationManager: LocationManager,
    private val mutableLocationSharedFlow: MutableSharedFlow<Location>,
) : LocationDataSource {

    override val locationSharedFlow: SharedFlow<Location> = mutableLocationSharedFlow.asSharedFlow()

    private val coroutineScope: CoroutineScope = CoroutineScope(dispatcherProvider.io() + SupervisorJob())
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            coroutineScope.launch {
                Timber.d("LocationDataSource - New Location ${locationResult.lastLocation}")
                locationResult.lastLocation.apply {
                    mutableLocationSharedFlow.emit(
                        Location(
                            latitude = latitude,
                            longitude = longitude,
                            altitude = altitude,
                            accuracy = accuracy,
                            speed = speed,
                            bearing = bearing,
                            time = time
                        )
                    )
                }
            }
        }
    }

    private var locationJob: Job? = null

    @SuppressLint("MissingPermission")
    override fun provideLastKnownLocationFlow(): Flow<Location?> = callbackFlow {
        fusedLocationProvider
            .lastLocation
            .addOnSuccessListener { location: AndroidLocation ->
                try {
                    trySend(
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
                } catch (exception: Exception) {
                    Timber.e("Error getting last known location: ${exception.message}")
                    trySend(null)
                }
            }
            .addOnFailureListener {
                trySend(null)
            }
        awaitClose { Timber.d("LocationDataSource - ProvideLastKnownLocationFlow - awaitClose") }
    }

    override fun hasLocationAvailable(): Either<Unit, Unit> = Either.catch(
        { Unit.left() },
        {
            when (LocationManagerCompat.isLocationEnabled(locationManager)) {
                true -> Unit.right()
                false -> Unit.left()
            }
        }
    )

    @SuppressLint("MissingPermission")
    override suspend fun startLocationProvider(locationParameters: LocationParameters) {
        if (locationJob == null)
            locationJob = coroutineScope.launch {
                Timber.d("LocationDataSource started with $locationParameters")
                fusedLocationProvider.let {
                    val locationRequest = LocationRequest.create()
                    locationRequest.apply {
                        priority = locationParameters.priority
                        fastestInterval = locationParameters.fastestInterval
                        smallestDisplacement = locationParameters.smallestDisplacement
                    }
                    it.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                }
            }
    }

    override suspend fun stopLocationProvider() {
        fusedLocationProvider.removeLocationUpdates(locationCallback)
        locationJob?.cancelAndJoin()
        locationJob = null
        Timber.d("LocationDataSource stopped")
    }
}
