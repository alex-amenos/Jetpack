package com.alxnophis.jetpack.location.tracker.data.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

internal class FusedLocationDataSourceImpl(
    private val context: Context
) : FusedLocationDataSource {

    @SuppressLint("MissingPermission")
    override fun getLastKnownLocation(block: (Location?) -> Unit) {
        if (isGPSEnabled() && (checkFineLocationPermission() || checkCoarseLocationPermission())) {
            LocationServices
                .getFusedLocationProviderClient(context)
                .lastLocation
                .addOnSuccessListener { location: Location? ->
                    block(location)
                }
        }
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun checkFineLocationPermission(): Boolean = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED

    private fun checkCoarseLocationPermission(): Boolean = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
}
