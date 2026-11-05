package com.alxnophis.jetpack.radio.domain.usecase

import arrow.core.Either
import com.alxnophis.jetpack.radio.domain.model.RadioError
import com.alxnophis.jetpack.radio.domain.model.RadioStation
import com.alxnophis.jetpack.radio.domain.repository.RadioStationsRepository

internal class GetSpanishStationsUseCase(
    private val repository: RadioStationsRepository,
) {
    suspend operator fun invoke(): Either<RadioError, List<RadioStation>> = repository.getSpanishStations()
}
