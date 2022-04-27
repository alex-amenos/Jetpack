package com.alxnophis.jetpack.location.tracker.data.data

internal interface FusedLocationDataSource {
    fun getLastKnownLocation(block: (android.location.Location?) -> Unit)
}
