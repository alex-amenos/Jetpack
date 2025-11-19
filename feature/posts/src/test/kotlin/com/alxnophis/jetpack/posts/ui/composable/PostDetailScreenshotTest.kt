package com.alxnophis.jetpack.posts.ui.composable

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.alxnophis.jetpack.posts.ui.composable.provider.PostDetailPreviewProvider
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
internal class PostDetailScreenshotTest(
    private val index: Int,
) {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun postDetailScreen() {
        val previewProvider = PostDetailPreviewProvider()
        val uiState = previewProvider.values.toList()[index]

        composeRule.setContent {
            PostDetailScreen(uiState)
        }

        composeRule
            .onRoot()
            .captureScreenshot(
                screenName = SCREEN_NAME,
                stateIndex = index,
            )
    }

    companion object {
        private const val SCREEN_NAME = "PostDetailScreen"

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "state_{0}")
        fun data(): List<Array<Any>> {
            val previewProvider = PostDetailPreviewProvider()
            return previewProvider.values
                .toList()
                .indices
                .map { arrayOf<Any>(it) }
        }
    }
}
