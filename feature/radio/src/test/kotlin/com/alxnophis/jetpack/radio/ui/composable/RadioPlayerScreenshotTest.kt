package com.alxnophis.jetpack.radio.ui.composable

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.alxnophis.jetpack.radio.playback.RadioPlaybackStatus
import com.alxnophis.jetpack.radio.ui.contract.RadioPlayerUiState
import com.alxnophis.jetpack.testing.screenshot.ScreenshotTestUtils.captureScreenshot
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
internal class RadioPlayerScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun radioPlayerPlaying() {
        val uiState = uiState(status = RadioPlaybackStatus.Playing, canPrevious = true, canNext = true)
        composeRule.setContent { RadioPlayerScreen(uiState, onBack = {}, handleEvent = {}) }

        composeRule.onRoot().captureScreenshot(screenName = "RadioPlayerScreen", stateIndex = 0)
    }

    @Test
    fun radioPlayerError() {
        val uiState = uiState(status = RadioPlaybackStatus.Error, canPrevious = false, canNext = false)
        composeRule.setContent { RadioPlayerScreen(uiState, onBack = {}, handleEvent = {}) }

        composeRule.onRoot().captureScreenshot(screenName = "RadioPlayerScreen", stateIndex = 1)
    }

    private fun uiState(
        status: RadioPlaybackStatus,
        canPrevious: Boolean,
        canNext: Boolean,
    ) = RadioPlayerUiState(
        stationName = "Catalunya Ràdio",
        artworkUri = null,
        status = status,
        canPrevious = canPrevious,
        canNext = canNext,
        sleepTimerRemainingMillis = null,
    )
}
