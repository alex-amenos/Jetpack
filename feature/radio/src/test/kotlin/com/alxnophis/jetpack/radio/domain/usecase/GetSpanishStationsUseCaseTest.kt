package com.alxnophis.jetpack.radio.domain.usecase

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.radio.domain.model.RadioError
import com.alxnophis.jetpack.radio.domain.model.Station
import com.alxnophis.jetpack.radio.domain.model.StationMother
import com.alxnophis.jetpack.radio.domain.repository.StationsRepository
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

internal class GetSpanishStationsUseCaseTest {
    @Test
    fun `GIVEN repository returns stations WHEN invoked THEN returns them`() =
        runTest {
            val stations = listOf(StationMother.create())
            val useCase = GetSpanishStationsUseCase(FakeRepository(stations.right()))

            useCase() shouldBeEqualTo stations.right()
        }

    @Test
    fun `GIVEN repository fails WHEN invoked THEN propagates the error`() =
        runTest {
            val useCase = GetSpanishStationsUseCase(FakeRepository(RadioError.ServerError.left()))

            useCase() shouldBeEqualTo RadioError.ServerError.left()
        }

    private class FakeRepository(
        private val result: Either<RadioError, List<Station>>,
    ) : StationsRepository {
        override suspend fun getSpanishStations() = result
    }
}
