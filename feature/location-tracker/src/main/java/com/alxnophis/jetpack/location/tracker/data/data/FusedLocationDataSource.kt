package com.alxnophis.jetpack.location.tracker.data.data

import android.location.Location

internal interface FusedLocationDataSource {
    fun getLastKnownLocation(block: (Location?) -> Unit)
}
