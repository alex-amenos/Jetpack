package com.alxnophis.jetpack.spacex.ui.viewmodel

import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.core.base.formatter.BaseDateFormatter
import com.alxnophis.jetpack.core.base.provider.BaseRandomProvider
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import com.alxnophis.jetpack.spacex.R
import com.alxnophis.jetpack.spacex.data.model.LaunchesError
import com.alxnophis.jetpack.spacex.data.repository.LaunchesRepository
import com.alxnophis.jetpack.spacex.mother.PastLaunchesDataModelMother
import com.alxnophis.jetpack.spacex.mother.PastLaunchesModelMother
import com.alxnophis.jetpack.spacex.ui.contract.LaunchesEvent
import com.alxnophis.jetpack.spacex.ui.contract.LaunchesState
import com.alxnophis.jetpack.testing.base.BaseViewModelUnitTest
import java.util.Date
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
internal class LaunchesViewModelUnitTest : BaseViewModelUnitTest() {

    private val dateFormatterMock: BaseDateFormatter = mock()
    private val randomProviderMock: BaseRandomProvider = mock()
    private val launchesRepositoryMock: LaunchesRepository = mock()
    private lateinit var viewModel: LaunchesViewModel

    @BeforeEach
    override fun beforeEach() {
        super.beforeEach()
        viewModel = viewModelMother()
    }

    @Test
    fun `WHEN init THEN validate initial state`() {
        runTest {
            viewModel.uiState.test {
                assertEquals(
                    initialLaunchesState,
                    awaitItem()
                )
                expectNoEvents()
            }
        }
    }

    @Test
    fun `WHEN init THEN validate get past launches event`() {
        runTest {
            val pastLaunchesDataModel = listOf(PastLaunchesDataModelMother.pastLaunch(launchDateUtc = Date()))
            whenever(dateFormatterMock.formatToReadableDateTime(any())).thenReturn("DATE")
            whenever(launchesRepositoryMock.getPastLaunches(false)).thenReturn(pastLaunchesDataModel.right())

            viewModel.uiEvent.test {
                assertEquals(
                    LaunchesEvent.GetPastLaunches,
                    awaitItem()
                )
                expectNoEvents()
            }
        }
    }

    @Test
    fun `WHEN refresh launches THEN validate refresh past launches event`() {
        runTest {
            val pastLaunchesDataModel = listOf(PastLaunchesDataModelMother.pastLaunch(launchDateUtc = Date()))
            whenever(dateFormatterMock.formatToReadableDateTime(any())).thenReturn("DATE")
            whenever(launchesRepositoryMock.getPastLaunches(false)).thenReturn(pastLaunchesDataModel.right())

            viewModel.setEvent(LaunchesEvent.RefreshPastLaunches)

            viewModel.uiEvent.test {
                assertEquals(
                    LaunchesEvent.GetPastLaunches,
                    awaitItem()
                )
                assertEquals(
                    LaunchesEvent.RefreshPastLaunches,
                    awaitItem()
                )
                expectNoEvents()
            }
        }
    }

    @Test
    fun `WHEN init AND get past launches succeed THEN validate state`() {
        runTest {
            val pastLaunchesDataModel = listOf(PastLaunchesDataModelMother.pastLaunch(launchDateUtc = Date()))
            val pastLaunches = listOf(PastLaunchesModelMother.pastLaunch(launchDateUtc = "DATE"))
            whenever(dateFormatterMock.formatToReadableDateTime(any())).thenReturn("DATE")
            whenever(launchesRepositoryMock.getPastLaunches(false)).thenReturn(pastLaunchesDataModel.right())

            viewModel.uiState.test {
                assertEquals(
                    initialLaunchesState,
                    awaitItem()
                )
                assertEquals(
                    initialLaunchesState.copy(isLoading = true),
                    awaitItem()
                )
                assertEquals(
                    initialLaunchesState.copy(
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
            whenever(randomProviderMock.mostSignificantBitsRandomUUID()).thenReturn(RANDOM_UUID_SIGNIFICANT_BITS)
            whenever(launchesRepositoryMock.getPastLaunches(false)).thenReturn(launchesError.left())

            viewModel.uiState.test {
                assertEquals(
                    initialLaunchesState,
                    awaitItem()
                )
                assertEquals(
                    initialLaunchesState.copy(isLoading = true),
                    awaitItem()
                )
                assertEquals(
                    initialLaunchesState.copy(
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

    private fun viewModelMother(
        initialState: LaunchesState = LaunchesState(),
        dateFormatter: BaseDateFormatter = dateFormatterMock,
        randomProvider: BaseRandomProvider = randomProviderMock,
        dispatcherProvider: DispatcherProvider = testDispatcherProvider,
        launchesRepository: LaunchesRepository = launchesRepositoryMock,
    ) = LaunchesViewModel(initialState, dateFormatter, randomProvider, dispatcherProvider, launchesRepository)

    companion object {
        private const val RANDOM_UUID_SIGNIFICANT_BITS: Long = 1L
        private const val STATUS_CODE_SERVER_ERROR = 500
        private val initialLaunchesState = LaunchesState()

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
            Arguments.of(LaunchesError.Unexpected, R.string.spacex_error_unknown),
        )
    }
}
