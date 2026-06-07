package com.alxnophis.jetpack.location.tracker.data.data

import android.annotation.SuppressLint
import android.location.LocationManager
import android.os.Looper
import androidx.core.location.LocationManagerCompat
import arrow.core.Either
import arrow.core.left
import com.alxnophis.jetpack.location.tracker.data.model.Location
import com.alxnophis.jetpack.location.tracker.data.model.LocationParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import android.location.Location as AndroidLocation

internal class LocationDataSourceImpl(
    private val fusedLocationProvider: FusedLocationProviderClient,
    private val locationManager: LocationManager,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : LocationDataSource {
    private val coroutineScope: CoroutineScope = CoroutineScope(defaultDispatcher + SupervisorJob())
    private val activeFlows = ConcurrentHashMap<LocationParameters, Flow<Location>>()

    @SuppressLint("MissingPermission")
    override fun getLocationFlow(parameters: LocationParameters): Flow<Location> =
        activeFlows.getOrPut(parameters) {
            callbackFlow {
                Timber.d("LocationDataSource started with $parameters")

                val locationCallback =
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            locationResult.lastLocation?.let { androidLocation ->
                                Timber.d("LocationDataSource - New Location $androidLocation")
                                trySend(
                                    Location(
                                        latitude = androidLocation.latitude,
                                        longitude = androidLocation.longitude,
                                        altitude = androidLocation.altitude,
                                        accuracy = androidLocation.accuracy,
                                        speed = androidLocation.speed,
                                        bearing = androidLocation.bearing,
                                        time = androidLocation.time,
                                    ),
                                )
                            }
                        }
                    }

                val locationRequest =
                    LocationRequest
                        .Builder(
                            parameters.priority,
                            parameters.fastestInterval,
                        ).setPriority(parameters.priority)
                        .build()

                fusedLocationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

                awaitClose {
                    Timber.d("LocationDataSource stopped")
                    fusedLocationProvider.removeLocationUpdates(locationCallback)
                }
            }.shareIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5000L),
                replay = 1,
            )
        }

    @SuppressLint("MissingPermission")
    override fun provideLastKnownLocationFlow(): Flow<Location?> =
        callbackFlow {
            fusedLocationProvider.lastLocation
                .addOnSuccessListener { location: AndroidLocation? ->
                    try {
                        location?.let {
                            trySend(
                                Location(
                                    latitude = it.latitude,
                                    longitude = it.longitude,
                                    altitude = it.altitude,
                                    accuracy = it.accuracy,
                                    speed = it.speed,
                                    bearing = it.bearing,
                                    time = it.time,
                                ),
                            )
                        } ?: trySend(null)
                        close()
                    } catch (exception: Exception) {
                        Timber.e("Error getting last known location: \${exception.message}")
                        trySend(null)
                        close(exception)
                    }
                }.addOnFailureListener {
                    trySend(null)
                    close()
                }
            awaitClose { Timber.d("LocationDataSource - ProvideLastKnownLocationFlow - awaitClose") }
        }

    override fun hasLocationAvailable(): Either<Unit, Unit> =
        Either
            .catch {
                when (LocationManagerCompat.isLocationEnabled(locationManager)) {
                    true -> Unit
                    false -> throw RuntimeException("Location is not enabled")
                }
            }.mapLeft { Unit.left() }
}
