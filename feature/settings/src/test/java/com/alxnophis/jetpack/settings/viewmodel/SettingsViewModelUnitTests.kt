package com.alxnophis.jetpack.settings.viewmodel

import app.cash.turbine.test
import arrow.core.right
import com.alxnophis.jetpack.settings.data.model.SettingsPreferences
import com.alxnophis.jetpack.settings.data.model.SettingsPreferencesMother
import com.alxnophis.jetpack.settings.data.repository.SettingsRepository
import com.alxnophis.jetpack.settings.ui.contract.MarketingOption
import com.alxnophis.jetpack.settings.ui.contract.SettingsUiEvent
import com.alxnophis.jetpack.settings.ui.contract.SettingsUiState
import com.alxnophis.jetpack.settings.ui.contract.Theme
import com.alxnophis.jetpack.settings.ui.viewmodel.SettingsViewModel
import com.alxnophis.jetpack.testing.base.BaseUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
private class SettingsViewModelUnitTests : BaseUnitTest() {
    private val settingsRepositoryMock: SettingsRepository = mock()

    @Test
    fun `GIVEN a user requests settings WHEN initialize viewmodel THEN assert UiState updates`() {
        runTest {
            val settingsPreferencesFlow =
                flowOf(
                    SettingsPreferencesMother.create(
                        notificationsEnabled = true,
                        hintsEnabled = true,
                        marketingOption = true,
                        themeOption = SettingsPreferences.ThemeOptions.DARK,
                    ),
                )
            whenever(settingsRepositoryMock.getSettingsFlow()).thenReturn(settingsPreferencesFlow)

            val viewModel = settingsViewModelMother()

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo SettingsUiState.initialState
                awaitItem() shouldBeEqualTo
                    SettingsUiState(
                        notificationsEnabled = true,
                        hintsEnabled = true,
                        marketingOption = MarketingOption.ALLOWED,
                        themeOption = Theme.DARK,
                    )
            }

            verify(settingsRepositoryMock).getSettingsFlow()
        }
    }

    @Test
    fun `GIVEN a user updates notification settings WHEN SetNotifications event THEN assert state change`() {
        runTest {
            val viewModel = settingsViewModelMother()
            val settingsPreferencesFlow = flowOf(SettingsPreferencesMother.create())
            whenever(settingsRepositoryMock.getSettingsFlow()).thenReturn(settingsPreferencesFlow)
            whenever(settingsRepositoryMock.updateNotificationsEnabled(true)).thenReturn(Unit.right())

            viewModel.handleEvent(SettingsUiEvent.SetNotifications)
            runCurrent()

            verify(settingsRepositoryMock).updateNotificationsEnabled(true)
        }
    }

    @Test
    fun `GIVEN a user updates hint settings WHEN SetHint event THEN assert state change`() {
        runTest {
            val viewModel = settingsViewModelMother()
            val settingsPreferencesFlow = flowOf(SettingsPreferencesMother.create())
            whenever(settingsRepositoryMock.getSettingsFlow()).thenReturn(settingsPreferencesFlow)
            whenever(settingsRepositoryMock.updateHintsEnabled(true)).thenReturn(Unit.right())

            viewModel.handleEvent(SettingsUiEvent.SetHint)
            runCurrent()

            verify(settingsRepositoryMock).updateHintsEnabled(true)
        }
    }

    @Test
    fun `GIVEN a user updates marketing option WHEN SetMarketingOption event THEN assert state change`() {
        runTest {
            val viewModel = settingsViewModelMother()
            val settingsPreferencesFlow = flowOf(SettingsPreferencesMother.create())
            whenever(settingsRepositoryMock.getSettingsFlow()).thenReturn(settingsPreferencesFlow)
            whenever(settingsRepositoryMock.updateMarketingOption(true)).thenReturn(Unit.right())

            viewModel.handleEvent(SettingsUiEvent.SetMarketingOption(MarketingOption.ALLOWED))
            runCurrent()

            verify(settingsRepositoryMock).updateMarketingOption(true)
        }
    }

    @Test
    fun `GIVEN a user updates theme option WHEN SetTheme event THEN assert state change`() {
        runTest {
            val viewModel = settingsViewModelMother()
            val settingsPreferencesFlow = flowOf(SettingsPreferencesMother.create())
            whenever(settingsRepositoryMock.getSettingsFlow()).thenReturn(settingsPreferencesFlow)
            whenever(settingsRepositoryMock.updateThemeOption(SettingsPreferences.ThemeOptions.DARK)).thenReturn(Unit.right())

            viewModel.handleEvent(SettingsUiEvent.SetTheme(Theme.DARK))
            runCurrent()

            verify(settingsRepositoryMock).updateThemeOption(SettingsPreferences.ThemeOptions.DARK)
        }
    }

    private fun settingsViewModelMother(
        settingsRepository: SettingsRepository = settingsRepositoryMock,
        initialState: SettingsUiState = SettingsUiState.initialState,
    ) = SettingsViewModel(
        settingsRepository = settingsRepository,
        initialState = initialState,
    )
}
