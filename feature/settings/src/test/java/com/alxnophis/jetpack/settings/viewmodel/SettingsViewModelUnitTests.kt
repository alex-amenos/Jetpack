package com.alxnophis.jetpack.settings.viewmodel

import app.cash.turbine.test
import com.alxnophis.jetpack.settings.ui.contract.MarketingOption
import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.settings.ui.contract.SettingsViewAction
import com.alxnophis.jetpack.settings.ui.contract.Theme
import com.alxnophis.jetpack.settings.ui.viewmodel.SettingsViewModel
import com.alxnophis.jetpack.testing.base.BaseUnitTest
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@ExperimentalCoroutinesApi
internal class SettingsViewModelUnitTests : BaseUnitTest() {

    @ParameterizedTest
    @MethodSource("testProvider")
    fun `WHEN action THEN assert state change`(
        action: SettingsViewAction,
        state: SettingsState
    ) {
        runTest {
            val viewModel = SettingsViewModel(initialState = SettingsState())

            viewModel.setAction(action)

            viewModel.uiState.test {
                assertEquals(
                    initialSettingsState,
                    awaitItem()
                )
                assertEquals(
                    state,
                    awaitItem()
                )
            }
        }
    }

    companion object {
        private val initialSettingsState = SettingsState()

        @JvmStatic
        private fun testProvider(): Stream<Arguments> = Stream.of(
            Arguments.of(
                SettingsViewAction.SetNotifications,
                initialSettingsState.copy(notificationsEnabled = !initialSettingsState.notificationsEnabled),
            ),
            Arguments.of(
                SettingsViewAction.SetHint,
                initialSettingsState.copy(hintsEnabled = !initialSettingsState.hintsEnabled),
            ),
            Arguments.of(
                SettingsViewAction.SetMarketingOption(MarketingOption.NOT_ALLOWED),
                initialSettingsState.copy(marketingOption = MarketingOption.NOT_ALLOWED),
            ),
            Arguments.of(
                SettingsViewAction.SetTheme(Theme.LIGHT),
                initialSettingsState.copy(themeOption = Theme.LIGHT),
            ),
            Arguments.of(
                SettingsViewAction.SetTheme(Theme.DARK),
                initialSettingsState.copy(themeOption = Theme.DARK),
            ),
        )
    }
}
