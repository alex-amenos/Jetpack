package com.alxnophis.jetpack.home.ui.viewmodel

import app.cash.turbine.test
import com.alxnophis.jetpack.home.domain.model.Feature
import com.alxnophis.jetpack.home.domain.model.NavigationItem
import com.alxnophis.jetpack.home.domain.usecase.GetNavigationItemsUseCase
import com.alxnophis.jetpack.home.ui.contract.HomeUiState
import com.alxnophis.jetpack.testing.base.BaseViewModelUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
            whenever(getNavigationItemsUseCaseMock()).thenReturn(GetNavigationItemsUseCase.defaultNavigationItems)

            val viewModel = viewModelMother()

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo HomeUiState.initialState
                expectNoEvents()
            }
        }

    @Test
    fun `GIVEN custom use case WHEN view model is initialized THEN initial state has custom navigation items`() =
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
            whenever(getNavigationItemsUseCaseMock()).thenReturn(customItems)

            val viewModel = viewModelMother(getNavigationItemsUseCase = getNavigationItemsUseCaseMock)

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo HomeUiState(navigationItems = customItems)
                expectNoEvents()
            }
        }

    @Test
    fun `GIVEN custom initial state WHEN view model is initialized THEN initial state matches provided state`() =
        runTest {
            val customInitialState =
                HomeUiState(
                    navigationItems =
                        listOf(
                            NavigationItem(
                                name = "Test Feature",
                                emoji = "🧪",
                                description = "Test description",
                                feature = Feature.Settings,
                            ),
                        ),
                )

            val viewModel = viewModelMother(initialState = customInitialState)

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo customInitialState
                expectNoEvents()
            }
        }

    private fun viewModelMother(
        getNavigationItemsUseCase: GetNavigationItemsUseCase = getNavigationItemsUseCaseMock,
        initialState: HomeUiState = HomeUiState(navigationItems = getNavigationItemsUseCase()),
    ) = HomeViewModel(
        getNavigationItemsUseCase = getNavigationItemsUseCase,
        initialState = initialState,
    )
}
