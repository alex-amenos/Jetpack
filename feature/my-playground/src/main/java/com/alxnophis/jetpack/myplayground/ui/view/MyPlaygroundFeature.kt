package com.alxnophis.jetpack.myplayground.ui.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.myplayground.di.injectMyPlayground
import com.alxnophis.jetpack.myplayground.ui.contract.MyPlaygroundEvent
import com.alxnophis.jetpack.myplayground.ui.viewmodel.MyPlaygroundViewModel
import org.koin.androidx.compose.getViewModel

@Composable
fun MyPlaygroundFeature(
    onBack: () -> Unit,
) {
    injectMyPlayground()
    val viewModel = getViewModel<MyPlaygroundViewModel>()
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
