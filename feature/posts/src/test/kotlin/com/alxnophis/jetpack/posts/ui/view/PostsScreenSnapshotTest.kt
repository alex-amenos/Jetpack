package com.alxnophis.jetpack.posts.ui.view

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.posts.domain.model.Post
import com.alxnophis.jetpack.posts.ui.contract.PostsState
import com.alxnophis.jetpack.testing.constants.PAPARAZZI_MAX_PERCENT_DIFFERENCE
import org.junit.Rule
import org.junit.Test

internal class PostsScreenSnapshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6,
        maxPercentDifference = PAPARAZZI_MAX_PERCENT_DIFFERENCE
    )

    @Test
    fun composable_content_loaded() {
        snapshot(
            state = PostsState(
                isLoading = false,
                posts = listOf(POST_1, POST_2),
                errorMessages = emptyList()
            )
        )
    }

    /**
     * TODO - Not working as expected, needed some delay? Loading not showed.
     */
    @Test
    fun composable_loading() {
        snapshot(
            state = PostsState(
                isLoading = true,
                posts = emptyList(),
                errorMessages = emptyList()
            )
        )
    }

    @Test
    fun composable_error() {
        snapshot(
            state = PostsState(
                isLoading = false,
                posts = emptyList(),
                errorMessages = listOf(ERROR_MESSAGE)
            )
        )
    }

    private fun snapshot(state: PostsState) {
        paparazzi.snapshot {
            PostsContent(
                state = state,
                handleEvent = {},
                navigateBack = {}
            )
        }
    }

    companion object {
        private const val LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
        private val ERROR_MESSAGE = ErrorMessage(id = 1, message = "Error message")
        private val POST_1 = Post(
            id = 1,
            userId = 1,
            title = "Post title 1",
            body = LOREM_IPSUM
        )
        private val POST_2 = Post(
            id = 2,
            userId = 2,
            title = "Post title 2",
            body = LOREM_IPSUM.repeat(10)
        )
    }
}
