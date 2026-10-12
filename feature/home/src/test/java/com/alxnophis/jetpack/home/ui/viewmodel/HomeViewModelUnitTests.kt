package com.alxnophis.jetpack.home.ui.viewmodel

import app.cash.turbine.test
import com.alxnophis.jetpack.home.domain.usecase.GetNavigationItemsUseCase
import com.alxnophis.jetpack.testing.base.BaseUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class HomeViewModelUnitTests : BaseUnitTest() {
    private val getNavigationItemsUseCase = GetNavigationItemsUseCase()

    @Test
    fun `GIVEN home view model WHEN initialized THEN initial state has navigation items immediately`() {
        runTest {
            val viewModel = HomeViewModel(getNavigationItemsUseCase)

            viewModel.uiState.test {
                val initialItem = awaitItem()
                initialItem.data.shouldNotBeEmpty()
                initialItem.data shouldBeEqualTo GetNavigationItemsUseCase.defaultNavigationItems
                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}
