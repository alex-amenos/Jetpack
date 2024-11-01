package com.alxnophis.jetpack.home.ui.view

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.home.di.injectHome
import com.alxnophis.jetpack.home.ui.contract.HomeEvent
import com.alxnophis.jetpack.home.ui.contract.HomeState
import com.alxnophis.jetpack.home.ui.viewmodel.HomeViewModel
import com.alxnophis.jetpack.router.screen.Route
import org.koin.androidx.compose.getViewModel

@Composable
fun HomeFeature(
    onNavigateTo: (Route) -> Unit,
    onBack: () -> Unit,
) {
    injectHome()
    val context = LocalContext.current
    val finish = { ActivityCompat.finishAffinity(context as Activity) }
    val viewModel = getViewModel<HomeViewModel>()
    val state: HomeState = viewModel.uiState.collectAsStateWithLifecycle().value
    HomeScreen(
        state = state,
        onEvent = { event ->
            when (event) {
                HomeEvent.GoBackRequested -> {
                    onBack()
                    finish()
                }

                is HomeEvent.NavigationRequested -> onNavigateTo(event.route)
                else -> viewModel.handleEvent(event)
            }
        },
    )
}
