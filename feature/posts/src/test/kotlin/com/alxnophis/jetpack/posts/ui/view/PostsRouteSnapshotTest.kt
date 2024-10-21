package com.alxnophis.jetpack.posts.ui.view

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.detectEnvironment
import com.alxnophis.jetpack.posts.data.model.Post
import com.alxnophis.jetpack.posts.ui.contract.PostUiError
import com.alxnophis.jetpack.posts.ui.contract.PostsUiState
import com.alxnophis.jetpack.testing.constants.PAPARAZZI_MAX_PERCENT_DIFFERENCE
import org.junit.Rule
import org.junit.Test

internal class PostsRouteSnapshotTest {
    // TODO - temporary fix
    // More info: https://github.com/cashapp/paparazzi/issues/1025
    @get:Rule
    val paparazzi =
        Paparazzi(
            deviceConfig = DeviceConfig.PIXEL_6,
            maxPercentDifference = PAPARAZZI_MAX_PERCENT_DIFFERENCE,
            environment =
                detectEnvironment().run {
                    copy(compileSdkVersion = 33, platformDir = platformDir.replace("34", "33"))
                },
        )

    @Test
    fun composable_content_loaded() {
        snapshot(
            state =
                PostsUiState(
                    isLoading = false,
                    posts = listOf(POST_1, POST_2),
                    error = null,
                ),
        )
    }

    @Test
    fun composable_loading() {
        snapshot(
            state =
                PostsUiState(
                    isLoading = true,
                    posts = emptyList(),
                    error = null,
                ),
        )
    }

    @Test
    fun composable_error() {
        snapshot(
            state =
                PostsUiState(
                    isLoading = false,
                    posts = emptyList(),
                    error = PostUiError.Network,
                ),
        )
    }

    private fun snapshot(state: PostsUiState) {
        paparazzi.snapshot {
            PostsScreen(state)
        }
    }

    companion object {
        private const val LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
        private val POST_1 =
            Post(
                id = 1,
                userId = 1,
                title = "Post title 1",
                body = LOREM_IPSUM,
            )
        private val POST_2 =
            Post(
                id = 2,
                userId = 2,
                title = "Post title 2",
                body = LOREM_IPSUM.repeat(10),
            )
    }
}
