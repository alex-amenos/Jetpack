package com.alxnophis.jetpack.home.ui.composable.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.alxnophis.jetpack.home.domain.model.Feature
import com.alxnophis.jetpack.home.domain.model.NavigationItem
import com.alxnophis.jetpack.home.ui.contract.HomeUiState

internal class HomePreviewProvider : PreviewParameterProvider<HomeUiState> {
    override val values: Sequence<HomeUiState>
        get() =
            sequenceOf(
                HomeUiState(
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
                ),
            )
}
