package com.alxnophis.jetpack.api.radiobrowser

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import com.alxnophis.jetpack.api.radiobrowser.model.StationApiModel
import retrofit2.http.GET
import retrofit2.http.Query

interface RadioBrowserStationsService {
    @GET("json/stations/bycountry/Spain")
    suspend fun getSpanishStations(
        @Query("limit") limit: Int = 30,
        @Query("order") order: String = "votes",
        @Query("reverse") reverse: Boolean = true,
    ): Either<CallError, List<StationApiModel>>
}
