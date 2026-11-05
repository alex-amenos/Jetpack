package com.alxnophis.jetpack.radio.ui.composable

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.radio.data.model.RadioStationMother
import com.alxnophis.jetpack.radio.ui.contract.RadioStationListStatus
import com.alxnophis.jetpack.radio.ui.contract.RadioStationListUiState
import com.alxnophis.jetpack.testing.screenshot.ScreenshotTestUtils.captureScreenshot
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
internal class RadioStationListScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun stationListContent() {
        val uiState =
            RadioStationListUiState(
                status = RadioStationListStatus.Content,
                stations =
                    listOf(
                        RadioStationMother.create(
                            id = "1",
                            name = "Cadena SER",
                            faviconUrl = null,
                            bitrate = 128,
                        ),
                        RadioStationMother.create(
                            id = "2",
                            name = "Catalunya Ràdio",
                            faviconUrl = null,
                            bitrate = 96,
                        ),
                    ).toImmutableList(),
            )
        composeRule.setContent {
            AppTheme {
                RadioStationListScreen(
                    uiState,
                    onBack = {},
                    onStationSelected = {},
                    handleEvent = {},
                )
            }
        }

        composeRule
            .onRoot()
            .captureScreenshot(
                screenName = "RadioStationListScreen",
                stateIndex = 0,
            )
    }

    @Test
    fun stationListError() {
        val uiState = RadioStationListUiState(status = RadioStationListStatus.Error, stations = persistentListOf())
        composeRule.setContent {
            AppTheme {
                RadioStationListScreen(
                    uiState,
                    onBack = {},
                    onStationSelected = {},
                    handleEvent = {},
                )
            }
        }

        composeRule
            .onRoot()
            .captureScreenshot(screenName = "RadioStationListScreen", stateIndex = 1)
    }
}
