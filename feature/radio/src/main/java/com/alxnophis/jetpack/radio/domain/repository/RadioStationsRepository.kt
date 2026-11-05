package com.alxnophis.jetpack.radio.domain.repository

import arrow.core.Either
import com.alxnophis.jetpack.radio.domain.model.RadioError
import com.alxnophis.jetpack.radio.domain.model.RadioStation

internal interface RadioStationsRepository {
    suspend fun getSpanishStations(): Either<RadioError, List<RadioStation>>
}
