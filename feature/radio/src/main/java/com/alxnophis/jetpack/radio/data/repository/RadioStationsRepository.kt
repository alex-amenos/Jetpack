package com.alxnophis.jetpack.radio.data.repository

import arrow.core.Either
import com.alxnophis.jetpack.radio.data.model.RadioError
import com.alxnophis.jetpack.radio.data.model.RadioStation

internal fun interface RadioStationsRepository {
    suspend fun getSpanishStations(): Either<RadioError, List<RadioStation>>
}
