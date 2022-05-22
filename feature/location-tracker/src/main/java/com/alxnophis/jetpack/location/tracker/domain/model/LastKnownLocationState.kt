package com.alxnophis.jetpack.location.tracker.domain.model

sealed class LastKnownLocationState {
    object Idle : LastKnownLocationState()
    object LocationNotAvailable : LastKnownLocationState()
    data class Location(
        val latitude: Double,
        val longitude: Double,
        val altitude: Double,
        val accuracy: Float,
        val speed: Float,
        val bearing: Float,
        val time: Long
    ) : LastKnownLocationState()
}
