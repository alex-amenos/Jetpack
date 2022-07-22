package com.alxnophis.jetpack.spacex.data.datasource

import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.api.spacex.SpacexApi
import com.alxnophis.jetpack.api.spacex.testbuilder.PastLaunchesApiTestBuilder
import com.alxnophis.jetpack.spacex.data.model.LaunchesError
import com.alxnophis.jetpack.spacex.data.model.PastLaunchesDataModel
import com.alxnophis.jetpack.testing.base.BaseUnitTest
import com.apollographql.apollo3.annotations.ApolloExperimental
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.exception.ApolloHttpException
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.apollographql.apollo3.exception.ApolloParseException
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
internal class LaunchesDataSourceUnitTest : BaseUnitTest() {

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
    @MethodSource("argumentTestProvider")
    fun `WHEN get past launches succeed THEN return a list of past launches`(
        hasToFetchDataFromNetworkOnly: Boolean
    ) {
        testScope.runTest {
            whenever(spacexApiMock.pastLaunches(hasToFetchDataFromNetworkOnly)).thenReturn(PastLaunchesApiTestBuilder.launches)

            val result = launchesDataSource.getPastLaunches(hasToFetchDataFromNetworkOnly)

            assertEquals(result, PAST_LAUNCHES.right())
        }
    }

    @ParameterizedTest
    @MethodSource("argumentTestProvider")
    fun `WHEN get past launches succeed with null THEN return an empty list of past launches`(
        hasToFetchDataFromNetworkOnly: Boolean
    ) {
        testScope.runTest {
            whenever(spacexApiMock.pastLaunches(hasToFetchDataFromNetworkOnly)).thenReturn(PastLaunchesApiTestBuilder.nullableLaunches)

            val result = launchesDataSource.getPastLaunches(hasToFetchDataFromNetworkOnly)

            assertEquals(result, emptyList<PastLaunchesDataModel>().right())
        }
    }

    @ParameterizedTest
    @MethodSource("argumentTestProvider")
    fun `WHEN get past launches fails with ApolloParseException then return parse error`(
        hasToFetchDataFromNetworkOnly: Boolean
    ) {
        testScope.runTest {
            whenever(spacexApiMock.pastLaunches(hasToFetchDataFromNetworkOnly)).thenThrow(ApolloParseException())

            val result = launchesDataSource.getPastLaunches(hasToFetchDataFromNetworkOnly)

            assertEquals(result, LaunchesError.Parse.left())
        }
    }

    @ParameterizedTest
    @MethodSource("argumentTestProvider")
    fun `WHEN get past launches fails with ApolloHttpException then return http error with status code`(
        hasToFetchDataFromNetworkOnly: Boolean
    ) {
        testScope.runTest {
            whenever(spacexApiMock.pastLaunches(hasToFetchDataFromNetworkOnly)).thenThrow(apolloHttpException)

            val result = launchesDataSource.getPastLaunches(hasToFetchDataFromNetworkOnly)

            assertEquals(result, LaunchesError.Http(500).left())
        }
    }

    @ParameterizedTest
    @MethodSource("argumentTestProvider")
    fun `WHEN get past launches fails with ApolloNetworkException then return network error`(
        hasToFetchDataFromNetworkOnly: Boolean
    ) {
        testScope.runTest {
            whenever(spacexApiMock.pastLaunches(hasToFetchDataFromNetworkOnly)).thenThrow(ApolloNetworkException())

            val result = launchesDataSource.getPastLaunches(hasToFetchDataFromNetworkOnly)

            assertEquals(result, LaunchesError.Network.left())
        }
    }

    @ParameterizedTest
    @MethodSource("argumentTestProvider")
    fun `WHEN get past launches fails with any other exception then return unknown error`(
        hasToFetchDataFromNetworkOnly: Boolean
    ) {
        testScope.runTest {
            whenever(spacexApiMock.pastLaunches(hasToFetchDataFromNetworkOnly)).thenThrow(ApolloException())

            val result = launchesDataSource.getPastLaunches(hasToFetchDataFromNetworkOnly)

            assertEquals(result, LaunchesError.Unknown.left())
        }
    }

    companion object {
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
        private val apolloHttpException = ApolloHttpException(
            statusCode = 500,
            headers = listOf(),
            body = null,
            message = "Server error",
            cause = null
        )

        @JvmStatic
        private fun argumentTestProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(true),
            Arguments.of(false)
        )
    }
}
