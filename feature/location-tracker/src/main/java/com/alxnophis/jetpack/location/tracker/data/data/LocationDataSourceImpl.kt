package com.alxnophis.jetpack.location.tracker.data.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Looper
import androidx.core.location.LocationManagerCompat
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.location.tracker.domain.model.Location
import com.alxnophis.jetpack.location.tracker.domain.model.LocationParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import android.location.Location as AndroidLocation

internal class LocationDataSourceImpl(
    private val context: Context,
    dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val mutableLocationSharedFlow: MutableSharedFlow<Location> = MutableSharedFlow(),
    private val mutableLastKnownLocationSharedFlow: MutableSharedFlow<Location> = MutableSharedFlow(),
) : LocationDataSource {

    override val locationSharedFlow: SharedFlow<Location> = mutableLocationSharedFlow.asSharedFlow()
    override val lastKnownLocationSharedFlow: SharedFlow<Location> = mutableLastKnownLocationSharedFlow.asSharedFlow()

    private val coroutineScope: CoroutineScope = CoroutineScope(dispatcherIO + SupervisorJob())
    private val locationManager: LocationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    private val locationProvider: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            coroutineScope.launch {
                Timber.d("LocationDataSource - New LastLocation ${locationResult.lastLocation}")
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
    override fun provideLastKnownLocation() {
        LocationServices
            .getFusedLocationProviderClient(context)
            .lastLocation
            .addOnSuccessListener { location: AndroidLocation ->
                coroutineScope.launch {
                    mutableLastKnownLocationSharedFlow.emit(
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
    override suspend fun start(locationParameters: LocationParameters) {
        if (locationJob == null)
            locationJob = coroutineScope.launch {
                Timber.d("LocationDataSource started with $locationParameters")
                locationProvider.let {
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

    override suspend fun stop() {
        locationProvider.removeLocationUpdates(locationCallback)
        locationJob?.cancelAndJoin()
        locationJob = null
        Timber.d("LocationDataSource stopped")
    }
}
