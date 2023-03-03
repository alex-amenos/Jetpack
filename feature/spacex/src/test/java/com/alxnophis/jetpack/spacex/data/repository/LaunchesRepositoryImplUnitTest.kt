package com.alxnophis.jetpack.spacex.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.api.spacex.SpacexApi
import com.alxnophis.jetpack.api.spacex.model.SpacexApiError
import com.alxnophis.jetpack.api.spacex.mother.LaunchMother
import com.alxnophis.jetpack.core.base.constants.EMPTY
import com.alxnophis.jetpack.spacex.LaunchesQuery
import com.alxnophis.jetpack.spacex.data.model.LaunchesError
import com.alxnophis.jetpack.spacex.data.model.PastLaunchDataModel
import com.alxnophis.jetpack.spacex.data.model.PastLaunchesDataModelMother
import com.alxnophis.jetpack.testing.base.BaseUnitTest
import com.apollographql.apollo3.annotations.ApolloExperimental
import java.util.stream.Stream
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ApolloExperimental
@ExperimentalCoroutinesApi
private class LaunchesRepositoryImplUnitTest : BaseUnitTest() {

    private val apiDataSourceMock: SpacexApi = mock()
    private lateinit var launchesRepository: LaunchesRepository

    override fun beforeEachCompleted() {
        launchesRepository = LaunchesRepositoryImpl(
            dispatcherProvider = testDispatcherProvider,
            apiDataSource = apiDataSourceMock
        )
    }

    @ParameterizedTest
    @MethodSource("pastLaunchesSuccessTestProvider")
    fun `WHEN get past launches succeed THEN return a list of past launches`(
        hasToFetchDataFromNetworkOnly: Boolean
    ) {
        runTest {
            whenever(apiDataSourceMock.pastLaunches(hasToFetchDataFromNetworkOnly)).thenReturn(LAUNCHES.right())

            val result: Either<LaunchesError, List<PastLaunchDataModel>> = launchesRepository.getPastLaunches(hasToFetchDataFromNetworkOnly)

            assertEquals(PAST_LAUNCHES.right(), result)
        }
    }

    @ParameterizedTest
    @MethodSource("pastLaunchesSuccessTestProvider")
    fun `WHEN get past launches succeed with null THEN return an empty list of past launches`(
        hasToFetchDataFromNetworkOnly: Boolean
    ) {
        runTest {
            whenever(apiDataSourceMock.pastLaunches(hasToFetchDataFromNetworkOnly)).thenReturn(EMPTY_PAST_LAUNCHES.right())

            val result: Either<LaunchesError, List<PastLaunchDataModel>> = launchesRepository.getPastLaunches(hasToFetchDataFromNetworkOnly)

            assertEquals(emptyList<PastLaunchDataModel>().right(), result)
        }
    }

    @ParameterizedTest
    @MethodSource("pastLaunchesErrorTestProvider")
    fun `WHEN get past launches failure then return launches error`(
        hasToFetchDataFromNetworkOnly: Boolean,
        exception: SpacexApiError,
        launchesError: LaunchesError
    ) {
        runTest {
            whenever(apiDataSourceMock.pastLaunches(hasToFetchDataFromNetworkOnly)).thenReturn(exception.left())

            val result: Either<LaunchesError, List<PastLaunchDataModel>> = launchesRepository.getPastLaunches(hasToFetchDataFromNetworkOnly)

            assertEquals(launchesError.left(), result)
        }
    }

    companion object {
        private const val STATUS_CODE_SERVER_ERROR = 500
        private val EMPTY_PAST_LAUNCHES = LaunchesQuery.Data(launches = emptyList())
        private val LAUNCHES = LaunchesQuery.Data(
            launches = listOf(LaunchMother(id = "id"))
        )
        private val PAST_LAUNCHES = listOf(
            PastLaunchesDataModelMother(
                id = "id",
                missionName = EMPTY,
                details = EMPTY,
                rocketName = EMPTY,
                launchSiteShort = EMPTY,
                launchSite = EMPTY,
                missionPatchSmallUrl = EMPTY,
                launchDateUtc = null
            )
        )

        @JvmStatic
        private fun pastLaunchesSuccessTestProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(true),
            Arguments.of(false)
        )

        @JvmStatic
        private fun pastLaunchesErrorTestProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(
                true,
                SpacexApiError.Parse,
                LaunchesError.Parse
            ),
            Arguments.of(
                false,
                SpacexApiError.Parse,
                LaunchesError.Parse
            ),
            Arguments.of(
                true,
                SpacexApiError.Http(STATUS_CODE_SERVER_ERROR),
                LaunchesError.Http(STATUS_CODE_SERVER_ERROR)
            ),
            Arguments.of(
                false,
                SpacexApiError.Http(STATUS_CODE_SERVER_ERROR),
                LaunchesError.Http(STATUS_CODE_SERVER_ERROR)
            ),
            Arguments.of(
                true,
                SpacexApiError.Network,
                LaunchesError.Network
            ),
            Arguments.of(
                false,
                SpacexApiError.Network,
                LaunchesError.Network
            ),
            Arguments.of(
                true,
                SpacexApiError.Unknown,
                LaunchesError.Unknown
            ),
            Arguments.of(
                false,
                SpacexApiError.Unknown,
                LaunchesError.Unknown
            ),
            Arguments.of(
                true,
                SpacexApiError.Unexpected,
                LaunchesError.Unexpected
            ),
            Arguments.of(
                false,
                SpacexApiError.Unexpected,
                LaunchesError.Unexpected
            )
        )
    }
}
