package com.alxnophis.jetpack.posts.ui.composable

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.alxnophis.jetpack.posts.ui.composable.provider.PostDetailPreviewProvider
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(RobolectricTestRunner::class)
internal class PostDetailScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()
    val previewProvider = PostDetailPreviewProvider()

    @Test
    fun postDetailScreenState0() {
        val uiState = previewProvider.values.elementAt(0)
        composeRule.setContent {
            PostDetailScreen(uiState)
        }
        composeRule
            .onRoot()
            .captureRoboImage("postDetailScreen_state_0")
    }

    @Test
    fun postDetailScreenState1() {
        val uiState = previewProvider.values.elementAt(1)
        composeRule.setContent {
            PostDetailScreen(uiState)
        }
        composeRule
            .onRoot()
            .captureRoboImage("postDetailScreen_state_1")
    }

    @Test
    fun postDetailScreenState2() {
        val uiState = previewProvider.values.elementAt(2)
        composeRule.setContent {
            PostDetailScreen(uiState)
        }
        composeRule
            .onRoot()
            .captureRoboImage("postDetailScreen_state_2")
    }

    @Test
    fun postDetailScreenState3() {
        val uiState = previewProvider.values.elementAt(3)
        composeRule.setContent {
            PostDetailScreen(uiState)
        }
        composeRule
            .onRoot()
            .captureRoboImage("postDetailScreen_state_3")
    }

    @Test
    fun postDetailScreenState4() {
        val uiState = previewProvider.values.elementAt(4)
        composeRule.setContent {
            PostDetailScreen(uiState)
        }
        composeRule
            .onRoot()
            .captureRoboImage("postDetailScreen_state_4")
    }

    @Test
    fun postDetailScreenState5() {
        val uiState = previewProvider.values.elementAt(5)
        composeRule.setContent {
            PostDetailScreen(uiState)
        }
        composeRule
            .onRoot()
            .captureRoboImage("postDetailScreen_state_5")
    }
}
