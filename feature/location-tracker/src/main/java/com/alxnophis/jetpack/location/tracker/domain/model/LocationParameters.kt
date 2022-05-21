package com.alxnophis.jetpack.location.tracker.domain.model

import com.google.android.gms.location.LocationRequest

private const val MINIMUM_DISTANCE_IN_METERS = 50F
private const val MINIMUM_TIME_IN_MILLISECONDS = 1000L

data class LocationParameters(
    val fastestInterval: Long = MINIMUM_TIME_IN_MILLISECONDS,
    val priority: Int = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
    val smallestDisplacement: Float = MINIMUM_DISTANCE_IN_METERS
)
