package com.alxnophis.jetpack.radio.ui.composable

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.alxnophis.jetpack.radio.domain.model.StationMother
import com.alxnophis.jetpack.radio.ui.contract.ListStatus
import com.alxnophis.jetpack.radio.ui.contract.StationListUiState
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
internal class StationListScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun stationListContent() {
        val uiState =
            StationListUiState(
                status = ListStatus.Content,
                stations =
                    listOf(
                        StationMother.create(id = "1", name = "Cadena SER", faviconUrl = null, bitrate = 128),
                        StationMother.create(id = "2", name = "Catalunya Ràdio", faviconUrl = null, bitrate = 96),
                    ).toImmutableList(),
            )
        composeRule.setContent { StationListScreen(uiState, onBack = {}, onStationSelected = {}, handleEvent = {}) }

        composeRule.onRoot().captureScreenshot(screenName = "StationListScreen", stateIndex = 0)
    }

    @Test
    fun stationListError() {
        val uiState = StationListUiState(status = ListStatus.Error, stations = persistentListOf())
        composeRule.setContent { StationListScreen(uiState, onBack = {}, onStationSelected = {}, handleEvent = {}) }

        composeRule.onRoot().captureScreenshot(screenName = "StationListScreen", stateIndex = 1)
    }
}
