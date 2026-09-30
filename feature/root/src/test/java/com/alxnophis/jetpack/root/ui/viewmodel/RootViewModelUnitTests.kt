package com.alxnophis.jetpack.root.ui.viewmodel

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever
import com.alxnophis.jetpack.core.connectivity.NetworkMonitor
import com.alxnophis.jetpack.root.ui.contract.RootUiState
import com.alxnophis.jetpack.settings.data.model.SettingsPreferences
import com.alxnophis.jetpack.settings.data.model.SettingsPreferences.ThemeOptions
import com.alxnophis.jetpack.settings.data.repository.SettingsRepository
import com.alxnophis.jetpack.testing.base.BaseUnitTest

@ExperimentalCoroutinesApi
private class RootViewModelUnitTests : BaseUnitTest() {
    private val settingsRepositoryMock: SettingsRepository = mock()
    private val networkMonitorMock: NetworkMonitor = mock()

    override fun beforeEachCompleted() {
        reset(settingsRepositoryMock, networkMonitorMock)
        whenever(settingsRepositoryMock.getSettingsFlow()).thenReturn(flowOf(SettingsPreferences.default))
        whenever(networkMonitorMock.isOnline).thenReturn(flowOf(true))
    }

    @Test
    fun `GIVEN default settings and online network WHEN initialize viewmodel THEN assert default UiState`() {
        runTest {
            val viewModel = RootViewModel(settingsRepositoryMock, networkMonitorMock)

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo RootUiState.initialState
                runCurrent()
                expectNoEvents()
            }
        }
    }

    @Test
    fun `GIVEN dark theme preferences WHEN initialize viewmodel THEN assert UiState emits dark theme`() {
        runTest {
            val darkSettings = SettingsPreferences.default.copy(themeOption = ThemeOptions.DARK)
            whenever(settingsRepositoryMock.getSettingsFlow()).thenReturn(flowOf(darkSettings))

            val viewModel = RootViewModel(settingsRepositoryMock, networkMonitorMock)

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo RootUiState.initialState
                runCurrent()
                awaitItem() shouldBeEqualTo RootUiState(themeOption = ThemeOptions.DARK, isOnline = true)
                expectNoEvents()
            }
        }
    }

    @Test
    fun `GIVEN offline network WHEN initialize viewmodel THEN assert UiState emits offline`() {
        runTest {
            whenever(networkMonitorMock.isOnline).thenReturn(flowOf(false))

            val viewModel = RootViewModel(settingsRepositoryMock, networkMonitorMock)

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo RootUiState.initialState
                runCurrent()
                awaitItem() shouldBeEqualTo RootUiState(themeOption = ThemeOptions.SYSTEM, isOnline = false)
                expectNoEvents()
            }
        }
    }

    @Test
    fun `GIVEN dynamic theme and network updates WHEN observing flows THEN assert UiState emits each change`() {
        runTest {
            val settingsFlow = MutableSharedFlow<SettingsPreferences>()
            val networkFlow = MutableSharedFlow<Boolean>()
            whenever(settingsRepositoryMock.getSettingsFlow()).thenReturn(settingsFlow)
            whenever(networkMonitorMock.isOnline).thenReturn(networkFlow)

            val viewModel = RootViewModel(settingsRepositoryMock, networkMonitorMock)

            viewModel.uiState.test {
                awaitItem() shouldBeEqualTo RootUiState.initialState

                settingsFlow.emit(SettingsPreferences.default.copy(themeOption = ThemeOptions.LIGHT))
                networkFlow.emit(true)
                runCurrent()
                awaitItem() shouldBeEqualTo RootUiState(themeOption = ThemeOptions.LIGHT, isOnline = true)

                networkFlow.emit(false)
                runCurrent()
                awaitItem() shouldBeEqualTo RootUiState(themeOption = ThemeOptions.LIGHT, isOnline = false)

                networkFlow.emit(true)
                runCurrent()
                awaitItem() shouldBeEqualTo RootUiState(themeOption = ThemeOptions.LIGHT, isOnline = true)

                expectNoEvents()
            }
        }
    }
}
