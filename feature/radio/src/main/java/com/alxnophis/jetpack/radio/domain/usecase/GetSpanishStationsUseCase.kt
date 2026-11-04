package com.alxnophis.jetpack.radio.domain.usecase

import arrow.core.Either
import com.alxnophis.jetpack.radio.domain.model.RadioError
import com.alxnophis.jetpack.radio.domain.model.Station
import com.alxnophis.jetpack.radio.domain.repository.StationsRepository

internal class GetSpanishStationsUseCase(
    private val repository: StationsRepository,
) {
    suspend operator fun invoke(): Either<RadioError, List<Station>> = repository.getSpanishStations()
}
