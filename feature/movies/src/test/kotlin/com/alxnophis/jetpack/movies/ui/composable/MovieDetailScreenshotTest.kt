package com.alxnophis.jetpack.movies.ui.composable

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import com.alxnophis.jetpack.movies.ui.composable.provider.MovieDetailStateProvider
import com.alxnophis.jetpack.testing.screenshot.ScreenshotTestUtils.captureScreenshot
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(ParameterizedRobolectricTestRunner::class)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
internal class MovieDetailScreenshotTest(
    private val index: Int,
) {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun movieDetailScreen() {
        val previewProvider = MovieDetailStateProvider()
        val uiState = previewProvider.values.toList()[index]

        composeRule.setContent {
            MovieDetailScreen(
                state = uiState,
                handleEvent = {},
            )
        }

        composeRule
            .onRoot()
            .captureScreenshot(
                screenName = SCREEN_NAME,
                stateIndex = index,
            )
    }

    companion object {
        private const val SCREEN_NAME = "MovieDetailScreen"

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "state_{0}")
        fun data(): List<Array<Any>> {
            val previewProvider = MovieDetailStateProvider()
            return previewProvider.values
                .toList()
                .indices
                .map { arrayOf<Any>(it) }
        }
    }
}
