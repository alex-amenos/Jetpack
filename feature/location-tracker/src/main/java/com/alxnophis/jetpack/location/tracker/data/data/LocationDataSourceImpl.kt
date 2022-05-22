package com.alxnophis.jetpack.location.tracker.data.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.location.LocationManagerCompat
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.location.tracker.domain.model.LastKnownLocationState
import com.alxnophis.jetpack.location.tracker.domain.model.LocationParameters
import com.alxnophis.jetpack.location.tracker.domain.model.LocationState
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

internal class LocationDataSourceImpl(
    private val context: Context,
    dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val mutableLocationStateFlow: MutableStateFlow<LocationState> = MutableStateFlow(LocationState.Idle),
    private val mutableLastKnownLocationStateFlow: MutableStateFlow<LastKnownLocationState> = MutableStateFlow(LastKnownLocationState.Idle)
) : LocationDataSource {

    override val locationState: StateFlow<LocationState> = mutableLocationStateFlow.asStateFlow()
    override val lastKnownLocationState: StateFlow<LastKnownLocationState> = mutableLastKnownLocationStateFlow.asStateFlow()

    private val coroutineScope: CoroutineScope = CoroutineScope(dispatcherIO + SupervisorJob())
    private val locationManager: LocationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    private val locationProvider: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            Timber.d("LocationDataSource - New LastLocation ${locationResult.lastLocation}")
            locationResult.lastLocation.apply {
                updateLocationState(
                    LocationState.Location(
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

    private var locationJob: Job? = null

    @SuppressLint("MissingPermission")
    override fun provideLastKnownLocation() {
        LocationServices
            .getFusedLocationProviderClient(context)
            .lastLocation
            .addOnSuccessListener { location: Location ->
                updateLastKnownLocationState(
                    LastKnownLocationState.Location(
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
            .addOnFailureListener {
                updateLastKnownLocationState(LastKnownLocationState.LocationNotAvailable)
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
                    it
                        .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                        .addOnFailureListener { updateLocationState(LocationState.LocationNotAvailable) }
                }
            }
    }

    private fun updateLocationState(locationState: LocationState) {
        mutableLocationStateFlow.update { locationState }
    }

    private fun updateLastKnownLocationState(locationState: LastKnownLocationState) {
        mutableLastKnownLocationStateFlow.update { locationState }
    }

    override suspend fun stop() {
        locationProvider.removeLocationUpdates(locationCallback)
        locationJob?.cancelAndJoin()
        locationJob = null
        Timber.d("LocationDataSource stopped")
    }
}
