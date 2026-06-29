package com.alxnophis.jetpack.movies.ui.composable

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.paging.compose.collectAsLazyPagingItems
import com.alxnophis.jetpack.movies.ui.composable.provider.MoviesPagingProvider
import com.alxnophis.jetpack.movies.ui.contract.MoviesState
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
internal class MoviesScreenshotTest(
    private val index: Int,
) {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun moviesScreen() {
        val pagingProvider = MoviesPagingProvider()
        val pagingFlow = pagingProvider.values.toList()[index]

        composeRule.setContent {
            val movies = pagingFlow.collectAsLazyPagingItems()
            MoviesScreen(
                state = MoviesState.initialState,
                movies = movies,
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
        private const val SCREEN_NAME = "MoviesScreen"

        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "state_{0}")
        fun data(): List<Array<Any>> {
            val pagingProvider = MoviesPagingProvider()
            return pagingProvider.values
                .toList()
                .indices
                .map { arrayOf<Any>(it) }
        }
    }
}
