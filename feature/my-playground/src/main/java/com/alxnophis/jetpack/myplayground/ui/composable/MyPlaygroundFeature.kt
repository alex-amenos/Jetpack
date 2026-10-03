package com.alxnophis.jetpack.myplayground.ui.composable

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.myplayground.ui.viewmodel.MyPlaygroundViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MyPlaygroundFeature(onBack: () -> Unit) {
    val viewModel = koinViewModel<MyPlaygroundViewModel>()
    MyPlaygroundScreen(
        state = viewModel.uiState.collectAsStateWithLifecycle().value,
        onBack = onBack,
        onEvent = viewModel::handleEvent,
    )
}
