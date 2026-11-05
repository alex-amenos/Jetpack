package com.alxnophis.jetpack.radio.data.repository

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import arrow.retrofit.adapter.either.networkhandling.HttpError
import arrow.retrofit.adapter.either.networkhandling.IOError
import arrow.retrofit.adapter.either.networkhandling.UnexpectedCallError
import com.alxnophis.jetpack.api.exception.NoConnectivityException
import com.alxnophis.jetpack.api.radiobrowser.RadioBrowserStationsService
import com.alxnophis.jetpack.radio.data.mapper.toRadioStationOrNull
import com.alxnophis.jetpack.radio.domain.model.RadioError
import com.alxnophis.jetpack.radio.domain.model.RadioStation
import com.alxnophis.jetpack.radio.domain.repository.RadioStationsRepository
import timber.log.Timber

private const val STATIONS_LIMIT = 50

internal class RadioStationsRepositoryImpl(
    private val service: RadioBrowserStationsService,
) : RadioStationsRepository {
    override suspend fun getSpanishStations(): Either<RadioError, List<RadioStation>> =
        service
            .getSpanishStations()
            .map { stations ->
                stations
                    .mapNotNull { it.toRadioStationOrNull() }
                    .distinctBy { it.streamUrl }
                    .take(STATIONS_LIMIT)
                    .sortedBy { it.name.lowercase() }
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
