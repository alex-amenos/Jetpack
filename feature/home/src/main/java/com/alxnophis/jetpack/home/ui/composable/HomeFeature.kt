package com.alxnophis.jetpack.home.ui.composable

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.home.domain.model.Feature
import com.alxnophis.jetpack.home.ui.contract.HomeEvent
import com.alxnophis.jetpack.home.ui.contract.HomeState
import com.alxnophis.jetpack.home.ui.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeFeature(
    onNavigateTo: (Feature) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val finish = { ActivityCompat.finishAffinity(context as Activity) }
    val viewModel = koinViewModel<HomeViewModel>()
    val state: HomeState = viewModel.uiState.collectAsStateWithLifecycle().value
    HomeScreen(
        state = state,
        onEvent = { event ->
            when (event) {
                HomeEvent.GoBackRequested -> {
                    onBack()
                    finish()
                }

                is HomeEvent.NavigationRequested -> onNavigateTo(event.feature)
                else -> viewModel.handleEvent(event)
            }
        },
    )
}
