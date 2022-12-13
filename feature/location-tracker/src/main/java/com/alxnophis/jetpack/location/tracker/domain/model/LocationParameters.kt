package com.alxnophis.jetpack.location.tracker.domain.model

private const val MINIMUM_DISTANCE_IN_METERS = 50F
private const val MINIMUM_TIME_IN_MILLISECONDS = 1000L
private const val PRIORITY_BALANCED_POWER_ACCURACY = 102

data class LocationParameters(
    val fastestInterval: Long = MINIMUM_TIME_IN_MILLISECONDS,
    val priority: Int = PRIORITY_BALANCED_POWER_ACCURACY,
    val smallestDisplacement: Float = MINIMUM_DISTANCE_IN_METERS
)
