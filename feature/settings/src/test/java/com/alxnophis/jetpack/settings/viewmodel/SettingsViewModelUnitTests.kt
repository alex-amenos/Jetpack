package com.alxnophis.jetpack.settings.viewmodel

import app.cash.turbine.test
import com.alxnophis.jetpack.settings.ui.contract.MarketingOption
import com.alxnophis.jetpack.settings.ui.contract.SettingsEvent
import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.settings.ui.contract.Theme
import com.alxnophis.jetpack.settings.ui.viewmodel.SettingsViewModel
import com.alxnophis.jetpack.testing.base.BaseUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@ExperimentalCoroutinesApi
private class SettingsViewModelUnitTests : BaseUnitTest() {
    @ParameterizedTest
    @MethodSource("testProvider")
    fun `WHEN event THEN assert state change`(
        event: SettingsEvent,
        state: SettingsState,
    ) {
        runTest {
            val viewModel = SettingsViewModel(initialState = SettingsState.initialState)

            viewModel.handleEvent(event)

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo initialSettingsState
                awaitItem() shouldBeEqualTo state
            }
        }
    }

    companion object {
        private val initialSettingsState = SettingsState.initialState

        @JvmStatic
        private fun testProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    SettingsEvent.SetNotifications,
                    initialSettingsState.copy(notificationsEnabled = !initialSettingsState.notificationsEnabled),
                ),
                Arguments.of(
                    SettingsEvent.SetHint,
                    initialSettingsState.copy(hintsEnabled = !initialSettingsState.hintsEnabled),
                ),
                Arguments.of(
                    SettingsEvent.SetMarketingOption(MarketingOption.NOT_ALLOWED),
                    initialSettingsState.copy(marketingOption = MarketingOption.NOT_ALLOWED),
                ),
                Arguments.of(
                    SettingsEvent.SetTheme(Theme.LIGHT),
                    initialSettingsState.copy(themeOption = Theme.LIGHT),
                ),
                Arguments.of(
                    SettingsEvent.SetTheme(Theme.DARK),
                    initialSettingsState.copy(themeOption = Theme.DARK),
                ),
            )
    }
}
