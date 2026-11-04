package com.alxnophis.jetpack.radio.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.retrofit.adapter.either.networkhandling.CallError
import com.alxnophis.jetpack.api.jsonplaceholder.model.CallErrorMother
import com.alxnophis.jetpack.api.radiobrowser.RadioBrowserStationsService
import com.alxnophis.jetpack.api.radiobrowser.model.StationApiModel
import com.alxnophis.jetpack.radio.domain.model.RadioError
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

internal class StationsRepositoryImplTest {
    @Test
    fun `GIVEN the API returns stations with valid and invalid URLs WHEN loading THEN only valid stations are returned`() =
        kotlinx.coroutines.test.runTest {
            val service =
                FakeStationsService(
                    response =
                        listOf(
                            apiModel("1", "https://ok.example.com/a.mp3"),
                            apiModel("2", null),
                            apiModel("3", "http://insecure.example.com/b.mp3"),
                        ).right(),
                )

            val result = StationsRepositoryImpl(service).getSpanishStations()

            result.map { stations -> stations.map { it.id } } shouldBeEqualTo listOf("1").right()
        }

    @Test
    fun `GIVEN the API returns more than 50 valid stations WHEN loading THEN returns the first 50`() =
        runTest {
            val apiStations = (1..80).map { apiModel("$it", "https://ok.example.com/$it.mp3") }
            val service = FakeStationsService(response = apiStations.right())

            val result = StationsRepositoryImpl(service).getSpanishStations()

            result.map { it.size } shouldBeEqualTo 50.right()
        }

    @Test
    fun `GIVEN the API returns duplicate stream urls WHEN loading THEN keeps one station per url`() =
        runTest {
            val service =
                FakeStationsService(
                    response =
                        listOf(
                            apiModel("1", "https://ok.example.com/a.mp3"),
                            apiModel("2", "https://ok.example.com/a.mp3"),
                        ).right(),
                )

            val result = StationsRepositoryImpl(service).getSpanishStations()

            result.map { stations -> stations.map { it.id } } shouldBeEqualTo listOf("1").right()
        }

    @Test
    fun `GIVEN stations returned by votes WHEN loading THEN they are sorted by name`() =
        runTest {
            val service =
                FakeStationsService(
                    response =
                        listOf(
                            StationApiModel(stationUuid = "1", name = "Rock FM", urlResolved = "https://ok.example.com/1.mp3"),
                            StationApiModel(stationUuid = "2", name = "cadena SER", urlResolved = "https://ok.example.com/2.mp3"),
                            StationApiModel(stationUuid = "3", name = "Antena 3", urlResolved = "https://ok.example.com/3.mp3"),
                        ).right(),
                )

            val result = StationsRepositoryImpl(service).getSpanishStations()

            result.map { stations -> stations.map { it.name } } shouldBeEqualTo listOf("Antena 3", "cadena SER", "Rock FM").right()
        }

    @Test
    fun `GIVEN the API returns an empty list WHEN loading THEN returns an empty right`() =
        kotlinx.coroutines.test.runTest {
            val service = FakeStationsService(response = emptyList<StationApiModel>().right())

            val result = StationsRepositoryImpl(service).getSpanishStations()

            result shouldBeEqualTo emptyList<Any>().right()
        }

    @Test
    fun `GIVEN no connectivity WHEN loading THEN returns NetworkUnavailable`() =
        kotlinx.coroutines.test.runTest {
            val service =
                FakeStationsService(
                    response =
                        CallErrorMother
                            .noConnectivityError()
                            .left(),
                )

            val result = StationsRepositoryImpl(service).getSpanishStations()

            result shouldBeEqualTo RadioError.NetworkUnavailable.left()
        }

    @Test
    fun `GIVEN an HTTP error WHEN loading THEN returns ServerError`() =
        kotlinx.coroutines.test.runTest {
            val service =
                FakeStationsService(
                    response =
                        CallErrorMother
                            .httpError(code = 500)
                            .left(),
                )

            val result = StationsRepositoryImpl(service).getSpanishStations()

            result shouldBeEqualTo RadioError.ServerError.left()
        }

    private fun apiModel(
        id: String,
        url: String?,
    ) = StationApiModel(stationUuid = id, name = "Station $id", urlResolved = url)

    private class FakeStationsService(
        private val response: Either<CallError, List<StationApiModel>>,
    ) : RadioBrowserStationsService {
        override suspend fun getSpanishStations(
            limit: Int,
            order: String,
            reverse: Boolean,
        ): Either<CallError, List<StationApiModel>> = response
    }
}
