package com.alxnophis.jetpack.posts.ui.composable

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.alxnophis.jetpack.posts.ui.composable.provider.PostsScreenPreviewProvider
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(RobolectricTestRunner::class)
internal class PostScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()
    val previewProvider = PostsScreenPreviewProvider()

    @Test
    fun postsScreenState0() {
        val uiState = previewProvider.values.elementAt(0)
        composeRule.setContent {
            PostsScreen(uiState)
        }
        composeRule
            .onRoot()
            .captureRoboImage("PostsScreen_state_0")
    }

    @Test
    fun postsScreenState1() {
        val uiState = previewProvider.values.elementAt(1)
        composeRule.setContent {
            PostsScreen(uiState)
        }
        composeRule
            .onRoot()
            .captureRoboImage("PostsScreen_state_1")
    }

    @Test
    fun postsScreenState2() {
        val uiState = previewProvider.values.elementAt(2)
        composeRule.setContent {
            PostsScreen(uiState)
        }
        composeRule
            .onRoot()
            .captureRoboImage("PostsScreen_state_2")
    }

    @Test
    fun postsScreenState3() {
        val uiState = previewProvider.values.elementAt(3)
        composeRule.setContent {
            PostsScreen(uiState)
        }
        composeRule
            .onRoot()
            .captureRoboImage("PostsScreen_state_3")
    }

    @Test
    fun postsScreenState4() {
        val uiState = previewProvider.values.elementAt(4)
        composeRule.setContent {
            PostsScreen(uiState)
        }
        composeRule
            .onRoot()
            .captureRoboImage("PostsScreen_state_4")
    }
}
