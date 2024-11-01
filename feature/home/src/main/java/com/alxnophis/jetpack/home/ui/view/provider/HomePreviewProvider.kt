package com.alxnophis.jetpack.home.ui.view.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.alxnophis.jetpack.home.domain.model.Feature
import com.alxnophis.jetpack.home.domain.model.NavigationItem
import com.alxnophis.jetpack.home.ui.contract.HomeState
import com.alxnophis.jetpack.home.ui.contract.NO_ERROR

internal class HomePreviewProvider : PreviewParameterProvider<HomeState> {
    override val values: Sequence<HomeState>
        get() =
            sequenceOf(
                HomeState(
                    isLoading = false,
                    data =
                        listOf(
                            NavigationItem(
                                name = "Screen 1",
                                emoji = "🐻",
                                description = "Lorem ipsum",
                                feature = Feature.Authentication,
                            ),
                            NavigationItem(
                                name = "Screen 2",
                                emoji = "🦊",
                                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
                                feature = Feature.Settings,
                            ),
                        ),
                    error = NO_ERROR,
                ),
            )
}
