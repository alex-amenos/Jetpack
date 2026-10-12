package com.alxnophis.jetpack.home.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.home.domain.model.Feature
import com.alxnophis.jetpack.home.ui.contract.HomeUiState
import com.alxnophis.jetpack.home.ui.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeFeature(
    onNavigateTo: (Feature) -> Unit,
    onBack: () -> Unit,
) {
    val viewModel = koinViewModel<HomeViewModel>()
    val uiState: HomeUiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        onNavigateTo = onNavigateTo,
        onBack = onBack,
    )
}
