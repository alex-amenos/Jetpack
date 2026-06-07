package com.alxnophis.jetpack.location.tracker.ui.composable

import android.Manifest
import android.content.Context
import com.alxnophis.jetpack.core.extensions.hasPermission

internal fun Context.hasLocationPermission(): Boolean =
    arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    ).any { permission ->
        this.hasPermission(permission)
    }
