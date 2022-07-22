package com.alxnophis.jetpack.spacex.data.datasource

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.api.spacex.SpacexApi
import com.alxnophis.jetpack.api.spacex.model.SpacexApiError
import com.alxnophis.jetpack.api.spacex.testbuilder.PastLaunchesApiTestBuilder
import com.alxnophis.jetpack.spacex.data.model.LaunchesError
import com.alxnophis.jetpack.spacex.data.model.PastLaunchesDataModel
import com.alxnophis.jetpack.testing.base.BaseUnitTest
import com.apollographql.apollo3.annotations.ApolloExperimental
import java.util.stream.Stream
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ApolloExperimental
@ExperimentalCoroutinesApi
internal class LaunchesDataSourceImplUnitTest : BaseUnitTest() {

    private val spacexApiMock: SpacexApi = mock()
    private lateinit var launchesDataSource: LaunchesDataSourceImpl

    @BeforeEach
    override fun beforeEach() {
        super.beforeEach()
        launchesDataSource = LaunchesDataSourceImpl(
            dispatcherProvider = testDispatcherProvider,
            spacexApi = spacexApiMock
        )
    }

    @ParameterizedTest
    @MethodSource("pastLaunchesSuccessTestProvider")
    fun `WHEN get past launches succeed THEN return a list of past launches`(
        hasToFetchDataFromNetworkOnly: Boolean
    ) {
        testScope.runTest {
            whenever(spacexApiMock.pastLaunches(hasToFetchDataFromNetworkOnly)).thenReturn(PastLaunchesApiTestBuilder.launches.right())

            val result: Either<LaunchesError, List<PastLaunchesDataModel>> = launchesDataSource.getPastLaunches(hasToFetchDataFromNetworkOnly)

            assertEquals(PAST_LAUNCHES.right(), result)
        }
    }

    @ParameterizedTest
    @MethodSource("pastLaunchesSuccessTestProvider")
    fun `WHEN get past launches succeed with null THEN return an empty list of past launches`(
        hasToFetchDataFromNetworkOnly: Boolean
    ) {
        testScope.runTest {
            whenever(spacexApiMock.pastLaunches(hasToFetchDataFromNetworkOnly)).thenReturn(PastLaunchesApiTestBuilder.nullableLaunches.right())

            val result: Either<LaunchesError, List<PastLaunchesDataModel>> = launchesDataSource.getPastLaunches(hasToFetchDataFromNetworkOnly)

            assertEquals(emptyList<PastLaunchesDataModel>().right(), result)
        }
    }

    @ParameterizedTest
    @MethodSource("pastLaunchesErrorTestProvider")
    fun `WHEN get past launches failure then return launches error`(
        hasToFetchDataFromNetworkOnly: Boolean,
        inputException: SpacexApiError,
        expectedError: LaunchesError
    ) {
        testScope.runTest {
            whenever(spacexApiMock.pastLaunches(hasToFetchDataFromNetworkOnly)).thenReturn(inputException.left())

            val result: Either<LaunchesError, List<PastLaunchesDataModel>> = launchesDataSource.getPastLaunches(hasToFetchDataFromNetworkOnly)

            assertEquals(expectedError.left(), result)
        }
    }

    companion object {
        private const val STATUS_CODE_SERVER_ERROR = 500
        private val PAST_LAUNCHES = listOf(
            PastLaunchesDataModel(
                id = "id",
                mission_name = "mission_name",
                details = "details",
                rocketName = "rocket_name",
                launchSiteShort = "site_name",
                launchSite = "site_name_long",
                mission_patch_small_url = "mission_patch_small",
                launch_date_utc = null
            )
        )
        private val apolloHttpException = SpacexApiError.Http(statusCode = STATUS_CODE_SERVER_ERROR)

        @JvmStatic
        private fun pastLaunchesSuccessTestProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(true),
            Arguments.of(false),
        )

        @JvmStatic
        private fun pastLaunchesErrorTestProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(true, SpacexApiError.Parse, LaunchesError.Parse),
            Arguments.of(false, SpacexApiError.Parse, LaunchesError.Parse),
            Arguments.of(true, apolloHttpException, LaunchesError.Http(STATUS_CODE_SERVER_ERROR)),
            Arguments.of(false, apolloHttpException, LaunchesError.Http(STATUS_CODE_SERVER_ERROR)),
            Arguments.of(true, SpacexApiError.Network, LaunchesError.Network),
            Arguments.of(false, SpacexApiError.Network, LaunchesError.Network),
            Arguments.of(true, SpacexApiError.Unknown, LaunchesError.Unknown),
            Arguments.of(false, SpacexApiError.Unknown, LaunchesError.Unknown),
            Arguments.of(true, SpacexApiError.Unexpected, LaunchesError.Unexpected),
            Arguments.of(false, SpacexApiError.Unexpected, LaunchesError.Unexpected),
        )
    }
}
