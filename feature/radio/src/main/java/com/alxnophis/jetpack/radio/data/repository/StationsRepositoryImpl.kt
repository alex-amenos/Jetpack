package com.alxnophis.jetpack.radio.data.repository

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import arrow.retrofit.adapter.either.networkhandling.HttpError
import arrow.retrofit.adapter.either.networkhandling.IOError
import arrow.retrofit.adapter.either.networkhandling.UnexpectedCallError
import com.alxnophis.jetpack.api.exception.NoConnectivityException
import com.alxnophis.jetpack.api.radiobrowser.RadioBrowserStationsService
import com.alxnophis.jetpack.radio.data.mapper.toStationOrNull
import com.alxnophis.jetpack.radio.domain.model.RadioError
import com.alxnophis.jetpack.radio.domain.model.Station
import com.alxnophis.jetpack.radio.domain.repository.StationsRepository
import timber.log.Timber

private const val STATIONS_LIMIT = 50

internal class StationsRepositoryImpl(
    private val service: RadioBrowserStationsService,
) : StationsRepository {
    override suspend fun getSpanishStations(): Either<RadioError, List<Station>> =
        service
            .getSpanishStations()
            .map { stations ->
                stations
                    .mapNotNull { it.toStationOrNull() }
                    .distinctBy { it.streamUrl }
                    .take(STATIONS_LIMIT)
            }.mapLeft { error ->
                Timber.e("GET Spanish stations error: $error")
                error.toRadioError()
            }

    private fun CallError.toRadioError(): RadioError =
        when (this) {
            is IOError -> if (cause is NoConnectivityException) RadioError.NetworkUnavailable else RadioError.ServerError
            is UnexpectedCallError -> RadioError.ServerError
            is HttpError -> RadioError.ServerError
        }
}
