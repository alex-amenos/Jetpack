package com.alxnophis.jetpack.testing.screenshot

import androidx.compose.ui.test.SemanticsNodeInteraction
import com.github.takahirom.roborazzi.captureRoboImage

/**
 * Screenshot test utilities for all modules.
 * Provides consistent naming and path management for screenshot tests.
 */
object ScreenshotTestUtils {
    /**
     * Captures a screenshot with a consistent naming pattern.
     *
     * @param screenName The name of the screen being tested (e.g., "PostsScreen", "PostDetailScreen")
     * @param stateIndex The index of the state from the PreviewParameterProvider
     */
    fun SemanticsNodeInteraction.captureScreenshot(
        screenName: String,
        stateIndex: Int,
    ) {
        captureRoboImage("${screenName}_state_$stateIndex.png")
    }

    /**
     * Captures a screenshot with a custom filename.
     *
     * @param fileName The custom filename for the screenshot (without extension)
     */
    fun SemanticsNodeInteraction.captureScreenshot(fileName: String) {
        captureRoboImage("$fileName.png")
    }

    /**
     * Generates a consistent screenshot filename.
     *
     * @param screenName The name of the screen
     * @param stateIndex The state index
     * @return The full path for the screenshot
     */
    fun getScreenshotPath(
        screenName: String,
        stateIndex: Int,
    ): String = "${screenName}_state_$stateIndex.png"

    /**
     * Generates a screenshot filename with custom suffix.
     *
     * @param screenName The name of the screen
     * @param suffix Custom suffix (e.g., "error", "loading", "success")
     * @return The full path for the screenshot
     */
    fun getScreenshotPath(
        screenName: String,
        suffix: String,
    ): String = "${screenName}_$suffix.png"
}
