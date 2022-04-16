package com.alxnophis.jetpack.location.tracker.data.repository

import com.alxnophis.jetpack.location.tracker.domain.model.Location
import kotlinx.coroutines.flow.StateFlow

interface LocationRepository {
    val locationStateFlow: StateFlow<Location?>
    suspend fun startLocationRequest(locationIntervalMillis: Long)
    suspend fun stopLocationRequest()
}
