package com.alxnophis.jetpack.location.tracker.data.model

data class Location(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val accuracy: Float,
    val speed: Float,
    val bearing: Float,
    val time: Long,
)
