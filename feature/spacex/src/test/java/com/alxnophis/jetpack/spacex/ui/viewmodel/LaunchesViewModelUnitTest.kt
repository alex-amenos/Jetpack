package com.alxnophis.jetpack.spacex.ui.viewmodel

import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import arrow.optics.copy
import com.alxnophis.jetpack.core.base.formatter.BaseDateFormatter
import com.alxnophis.jetpack.core.base.provider.BaseRandomProvider
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.spacex.R
import com.alxnophis.jetpack.spacex.data.model.LaunchesError
import com.alxnophis.jetpack.spacex.data.model.PastLaunchesDataModelMother
import com.alxnophis.jetpack.spacex.data.repository.LaunchesRepository
import com.alxnophis.jetpack.spacex.ui.contract.LaunchesEvent
import com.alxnophis.jetpack.spacex.ui.contract.LaunchesState
import com.alxnophis.jetpack.spacex.ui.contract.errorMessages
import com.alxnophis.jetpack.spacex.ui.model.PastLaunchesModelMother
import com.alxnophis.jetpack.testing.base.BaseViewModelUnitTest
import java.util.Date
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
private class LaunchesViewModelUnitTest : BaseViewModelUnitTest() {

    private val dateFormatterMock: BaseDateFormatter = mock()
    private val randomProviderMock: BaseRandomProvider = mock()
    private val launchesRepositoryMock: LaunchesRepository = mock()

    @Test
    fun `WHEN init THEN validate initial state with past launches`() {
        runTest {
            val pastLaunchesDataModel = listOf(PastLaunchesDataModelMother(launchDateUtc = Date()))
            val pastLaunches = listOf(PastLaunchesModelMother.invoke(launchDateUtc = "DATE"))
            val viewModel = viewModelMother()
            whenever(dateFormatterMock.formatToReadableDateTime(any())).thenReturn("DATE")
            whenever(launchesRepositoryMock.getPastLaunches(false)).thenReturn(pastLaunchesDataModel.right())

            viewModel.handleEvent(LaunchesEvent.Initialized)

            viewModel.uiState.test {
                assertEquals(
                    LaunchesState.initialState,
                    awaitItem()
                )
                assertEquals(
                    LaunchesState.initialState.copy(isLoading = true),
                    awaitItem()
                )
                assertEquals(
                    LaunchesState.initialState.copy(
                        isLoading = false,
                        pastLaunches = pastLaunches
                    ),
                    awaitItem()
                )
                expectNoEvents()
            }
        }
    }

    @Test
    fun `WHEN init THEN validate get past launches not fetched from network only`() {
        runTest {
            val pastLaunchesDataModel = listOf(PastLaunchesDataModelMother(launchDateUtc = Date()))
            val viewModel = viewModelMother()
            whenever(dateFormatterMock.formatToReadableDateTime(any())).thenReturn("DATE")
            whenever(launchesRepositoryMock.getPastLaunches(false)).thenReturn(pastLaunchesDataModel.right())

            viewModel.handleEvent(LaunchesEvent.Initialized)
            runCurrent()

            verify(launchesRepositoryMock).getPastLaunches(false)
        }
    }

    @Test
    fun `WHEN refresh launches THEN validate get past launches fetched from network only after the init`() {
        runTest {
            val pastLaunchesDataModel = listOf(PastLaunchesDataModelMother(launchDateUtc = Date()))
            val viewModel = viewModelMother()
            whenever(dateFormatterMock.formatToReadableDateTime(any())).thenReturn("DATE")
            whenever(launchesRepositoryMock.getPastLaunches(any())).thenReturn(pastLaunchesDataModel.right())

            viewModel.handleEvent(LaunchesEvent.RefreshPastLaunchesRequested)
            runCurrent()

            verify(launchesRepositoryMock).getPastLaunches(true)
        }
    }

    @Test
    fun `WHEN init AND get past launches succeed THEN validate state`() {
        runTest {
            val pastLaunchesDataModel = listOf(PastLaunchesDataModelMother(launchDateUtc = Date()))
            val pastLaunches = listOf(PastLaunchesModelMother.invoke(launchDateUtc = "DATE"))
            val viewModel = viewModelMother()
            whenever(dateFormatterMock.formatToReadableDateTime(any())).thenReturn("DATE")
            whenever(launchesRepositoryMock.getPastLaunches(false)).thenReturn(pastLaunchesDataModel.right())

            viewModel.handleEvent(LaunchesEvent.Initialized)

            viewModel.uiState.test {
                assertEquals(
                    LaunchesState.initialState,
                    awaitItem()
                )
                assertEquals(
                    LaunchesState.initialState.copy(isLoading = true),
                    awaitItem()
                )
                assertEquals(
                    LaunchesState.initialState.copy(
                        isLoading = false,
                        pastLaunches = pastLaunches
                    ),
                    awaitItem()
                )
                expectNoEvents()
            }
        }
    }

    @ParameterizedTest
    @MethodSource("pastLaunchesTestProviderErrors")
    fun `WHEN init AND get past launches fails THEN validate state errors`(
        launchesError: LaunchesError,
        errorResId: Int
    ) {
        runTest {
            val viewModel = viewModelMother()
            whenever(randomProviderMock.mostSignificantBitsRandomUUID()).thenReturn(RANDOM_UUID_SIGNIFICANT_BITS)
            whenever(launchesRepositoryMock.getPastLaunches(false)).thenReturn(launchesError.left())

            viewModel.handleEvent(LaunchesEvent.Initialized)

            viewModel.uiState.test {
                assertEquals(
                    LaunchesState.initialState,
                    awaitItem()
                )
                assertEquals(
                    LaunchesState.initialState.copy(isLoading = true),
                    awaitItem()
                )
                assertEquals(
                    LaunchesState.initialState.copy(
                        isLoading = false,
                        errorMessages = listOf(
                            ErrorMessage(
                                id = RANDOM_UUID_SIGNIFICANT_BITS,
                                messageId = errorResId
                            )
                        )
                    ),
                    awaitItem()
                )
                expectNoEvents()
            }
        }
    }

    @Test
    fun `WHEN dismiss error THEN remove error from ui state`() {
        runTest {
            val viewModel = viewModelMother(
                initialState = LaunchesState.initialState.copy {
                    LaunchesState.errorMessages set listOf(
                        ErrorMessage(
                            id = RANDOM_UUID_SIGNIFICANT_BITS,
                            messageId = R.string.spacex_error_unknown
                        )
                    )
                }
            )
            whenever(randomProviderMock.mostSignificantBitsRandomUUID()).thenReturn(RANDOM_UUID_SIGNIFICANT_BITS)
            whenever(launchesRepositoryMock.getPastLaunches(false)).thenReturn(LaunchesError.Unknown.left())

            viewModel.handleEvent(LaunchesEvent.DismissErrorRequested(RANDOM_UUID_SIGNIFICANT_BITS))
            runCurrent()

            viewModel.uiState.test {
                assertEquals(
                    LaunchesState.initialState,
                    awaitItem()
                )
                expectNoEvents()
            }
        }
    }

    @Test
    fun `WHEN navigate back event THEN throw IllegalStateException`() {
        assertThrows<IllegalStateException> {
            runTest {
                viewModelMother().handleEvent(LaunchesEvent.GoBackRequested)
                runCurrent()
            }
        }
    }

    private fun viewModelMother(
        initialState: LaunchesState = LaunchesState.initialState,
        dateFormatter: BaseDateFormatter = dateFormatterMock,
        randomProvider: BaseRandomProvider = randomProviderMock,
        launchesRepository: LaunchesRepository = launchesRepositoryMock
    ) = LaunchesViewModel(
        dateFormatter,
        randomProvider,
        launchesRepository,
        initialState
    )

    companion object {
        private const val RANDOM_UUID_SIGNIFICANT_BITS: Long = 1L
        private const val STATUS_CODE_SERVER_ERROR: Int = 500

        @JvmStatic
        private fun pastLaunchesTestProviderErrors(): Stream<Arguments> = Stream.of(
            Arguments.of(LaunchesError.Parse, R.string.spacex_error_parse),
            Arguments.of(LaunchesError.Parse, R.string.spacex_error_parse),
            Arguments.of(LaunchesError.Http(STATUS_CODE_SERVER_ERROR), R.string.spacex_error_http),
            Arguments.of(LaunchesError.Http(STATUS_CODE_SERVER_ERROR), R.string.spacex_error_http),
            Arguments.of(LaunchesError.Network, R.string.spacex_error_network),
            Arguments.of(LaunchesError.Network, R.string.spacex_error_network),
            Arguments.of(LaunchesError.Unknown, R.string.spacex_error_unknown),
            Arguments.of(LaunchesError.Unknown, R.string.spacex_error_unknown),
            Arguments.of(LaunchesError.Unexpected, R.string.spacex_error_unknown),
            Arguments.of(LaunchesError.Unexpected, R.string.spacex_error_unknown)
        )
    }
}
