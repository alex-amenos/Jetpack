package com.alxnophis.jetpack.myplayground.ui.composable

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.myplayground.di.injectMyPlayground
import com.alxnophis.jetpack.myplayground.ui.contract.MyPlaygroundEvent
import com.alxnophis.jetpack.myplayground.ui.viewmodel.MyPlaygroundViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MyPlaygroundFeature(onBack: () -> Unit) {
    injectMyPlayground()
    val viewModel = koinViewModel<MyPlaygroundViewModel>()
    MyPlaygroundScreen(
        state = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            when (event) {
                MyPlaygroundEvent.GoBackRequested -> onBack()
                else -> viewModel.handleEvent(event)
            }
        },
    )
}
