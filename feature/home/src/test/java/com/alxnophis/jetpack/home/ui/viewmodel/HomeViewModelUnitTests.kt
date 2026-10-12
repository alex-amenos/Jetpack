package com.alxnophis.jetpack.home.ui.viewmodel

import app.cash.turbine.test
import arrow.core.left
import arrow.core.right
import com.alxnophis.jetpack.home.R
import com.alxnophis.jetpack.home.domain.model.Feature
import com.alxnophis.jetpack.home.domain.model.NavigationError
import com.alxnophis.jetpack.home.domain.model.NavigationItem
import com.alxnophis.jetpack.home.domain.usecase.GetNavigationItemsUseCase
import com.alxnophis.jetpack.home.ui.contract.HomeEvent
import com.alxnophis.jetpack.home.ui.contract.HomeState
import com.alxnophis.jetpack.home.ui.contract.HomeUiError
import com.alxnophis.jetpack.testing.base.BaseViewModelUnitTest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
internal class HomeViewModelUnitTests : BaseViewModelUnitTest() {
    private val getNavigationItemsUseCaseMock: GetNavigationItemsUseCase = mock()

    @Test
    fun `GIVEN default setup WHEN view model is initialized THEN initial state has navigation items immediately`() =
        runTest {
            val viewModel = viewModelMother()

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo HomeState.initialState
                expectNoEvents()
            }
        }

    @Test
    fun `GIVEN empty initial data WHEN state flow is collected THEN navigation items are loaded successfully`() =
        runTest {
            val customItems =
                listOf(
                    NavigationItem(
                        name = "Custom Feature",
                        emoji = "🚀",
                        description = "Custom test description",
                        feature = Feature.Posts,
                    ),
                )
            whenever(
                context(testDispatcher) {
                    getNavigationItemsUseCaseMock()
                },
            ).thenReturn(customItems.right())

            val emptyInitialState =
                HomeState(
                    isLoading = false,
                    data = emptyList(),
                    error = null,
                )
            val viewModel = viewModelMother(initialState = emptyInitialState)

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo emptyInitialState
                runCurrent()
                awaitItem() shouldBeEqualTo emptyInitialState.copy(isLoading = false, data = customItems)
                expectNoEvents()
            }
        }

    @Test
    fun `GIVEN empty initial data AND use case fails WHEN state flow is collected THEN updates error state`() =
        runTest {
            whenever(
                context(testDispatcher) {
                    getNavigationItemsUseCaseMock()
                },
            ).thenReturn(NavigationError.Unknown.left())

            val emptyInitialState =
                HomeState(
                    isLoading = false,
                    data = emptyList(),
                    error = null,
                )
            val viewModel = viewModelMother(initialState = emptyInitialState)

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo emptyInitialState
                runCurrent()
                val errorState = awaitItem()
                val expectedError =
                    HomeUiError(
                        id = errorState.error?.id ?: 0L,
                        messageRes = R.string.home_error_loading_navigation_items,
                    )
                errorState shouldBeEqualTo emptyInitialState.copy(isLoading = false, error = expectedError)
                expectNoEvents()
            }
        }

    @Test
    fun `GIVEN state with error WHEN ErrorDismissRequested with matching errorId THEN error is dismissed`() =
        runTest {
            val testError =
                HomeUiError(
                    id = TEST_ERROR_ID,
                    messageRes = R.string.home_error_loading_navigation_items,
                )
            val stateWithError =
                HomeState(
                    isLoading = false,
                    data = GetNavigationItemsUseCase.defaultNavigationItems,
                    error = testError,
                )
            val viewModel = viewModelMother(initialState = stateWithError)

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo stateWithError

                viewModel.handleEvent(HomeEvent.ErrorDismissRequested(TEST_ERROR_ID))
                runCurrent()

                awaitItem() shouldBeEqualTo stateWithError.copy(error = null)
                expectNoEvents()
            }
        }

    @Test
    fun `GIVEN state with error WHEN ErrorDismissRequested with different errorId THEN error remains unchanged`() =
        runTest {
            val testError =
                HomeUiError(
                    id = TEST_ERROR_ID,
                    messageRes = R.string.home_error_loading_navigation_items,
                )
            val stateWithError =
                HomeState(
                    isLoading = false,
                    data = GetNavigationItemsUseCase.defaultNavigationItems,
                    error = testError,
                )
            val viewModel = viewModelMother(initialState = stateWithError)

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo stateWithError

                viewModel.handleEvent(HomeEvent.ErrorDismissRequested(errorId = 9999L))
                runCurrent()

                expectNoEvents()
            }
        }

    private fun viewModelMother(
        getNavigationItemsUseCase: GetNavigationItemsUseCase = getNavigationItemsUseCaseMock,
        initialState: HomeState = HomeState.initialState,
        ioDispatcher: CoroutineDispatcher = testDispatcher,
    ) = HomeViewModel(
        getNavigationItemsUseCase = getNavigationItemsUseCase,
        initialState = initialState,
        ioDispatcher = ioDispatcher,
    )

    private companion object {
        const val TEST_ERROR_ID = 42L
    }
}
