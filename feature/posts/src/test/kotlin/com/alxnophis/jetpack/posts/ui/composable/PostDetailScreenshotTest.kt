package com.alxnophis.jetpack.posts.ui.composable

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.alxnophis.jetpack.posts.ui.contract.PostDetailUiState
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(RobolectricTestRunner::class)
class PostDetailScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun postDetailScreenInitialState() {
        composeRule.setContent {
            PostDetailScreen(PostDetailUiState.initialState)
        }
        composeRule
            .onRoot()
            .captureRoboImage()
    }
}
