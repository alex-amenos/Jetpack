package com.alxnophis.jetpack.spacex.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.spacex.data.datasource.LaunchesDataSource
import com.alxnophis.jetpack.spacex.data.model.LaunchesError
import com.alxnophis.jetpack.spacex.data.model.PastLaunchDataModel
import com.alxnophis.jetpack.spacex.mother.PastLaunchesDataModelMother
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
internal class LaunchesRepositoryUnitTest : BaseUnitTest() {

    private val launchesDataSourceMock: LaunchesDataSource = mock()
    private lateinit var launchesRepository: LaunchesRepository

    @BeforeEach
    override fun beforeEach() {
        super.beforeEach()
        launchesRepository = LaunchesRepositoryImpl(
            launchesDataSource = launchesDataSourceMock
        )
    }

    @ParameterizedTest
    @MethodSource("pastLaunchesSuccessTestProvider")
    fun `WHEN get past launches succeed THEN return a list of past launches`(
        hasToFetchDataFromNetworkOnly: Boolean
    ) {
        testScope.runTest {
            whenever(launchesDataSourceMock.getPastLaunches(hasToFetchDataFromNetworkOnly)).thenReturn(PAST_LAUNCHES.right())

            val result: Either<LaunchesError, List<PastLaunchDataModel>> = launchesRepository.getPastLaunches(hasToFetchDataFromNetworkOnly)

            assertEquals(PAST_LAUNCHES.right(), result)
        }
    }

    @ParameterizedTest
    @MethodSource("pastLaunchesSuccessTestProvider")
    fun `WHEN get past launches succeed with null THEN return an empty list of past launches`(
        hasToFetchDataFromNetworkOnly: Boolean
    ) {
        testScope.runTest {
            whenever(launchesDataSourceMock.getPastLaunches(hasToFetchDataFromNetworkOnly)).thenReturn(emptyList<PastLaunchDataModel>().right())

            val result: Either<LaunchesError, List<PastLaunchDataModel>> = launchesRepository.getPastLaunches(hasToFetchDataFromNetworkOnly)

            assertEquals(emptyList<PastLaunchDataModel>().right(), result)
        }
    }

    @ParameterizedTest
    @MethodSource("pastLaunchesErrorTestProvider")
    fun `WHEN get past launches failure then return launches error`(
        hasToFetchDataFromNetworkOnly: Boolean,
        exception: LaunchesError,
    ) {
        testScope.runTest {
            whenever(launchesDataSourceMock.getPastLaunches(hasToFetchDataFromNetworkOnly)).thenReturn(exception.left())

            val result: Either<LaunchesError, List<PastLaunchDataModel>> = launchesRepository.getPastLaunches(hasToFetchDataFromNetworkOnly)

            assertEquals(exception.left(), result)
        }
    }

    companion object {
        private const val STATUS_CODE_SERVER_ERROR = 500
        private val PAST_LAUNCHES = listOf(PastLaunchesDataModelMother.pastLaunch())

        @JvmStatic
        private fun pastLaunchesSuccessTestProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(true),
            Arguments.of(false),
        )

        @JvmStatic
        private fun pastLaunchesErrorTestProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(true, LaunchesError.Parse),
            Arguments.of(false, LaunchesError.Parse),
            Arguments.of(true, LaunchesError.Http(STATUS_CODE_SERVER_ERROR)),
            Arguments.of(false, LaunchesError.Http(STATUS_CODE_SERVER_ERROR)),
            Arguments.of(true, LaunchesError.Network),
            Arguments.of(false, LaunchesError.Network),
            Arguments.of(true, LaunchesError.Unknown),
            Arguments.of(false, LaunchesError.Unknown),
            Arguments.of(true, LaunchesError.Unexpected),
            Arguments.of(false, LaunchesError.Unexpected),
        )
    }
}
