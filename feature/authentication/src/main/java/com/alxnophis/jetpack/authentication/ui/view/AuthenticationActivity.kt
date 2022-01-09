package com.alxnophis.jetpack.authentication.ui.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import com.alxnophis.jetpack.authentication.di.injectAuthentication
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEffect
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.authentication.ui.viewmodel.AuthenticationViewModel
import com.alxnophis.jetpack.core.base.activity.BaseActivity
import com.alxnophis.jetpack.core.extensions.repeatOnLifecycleResumed
import com.alxnophis.jetpack.core.extensions.repeatOnLifecycleStarted
import com.alxnophis.jetpack.core.ui.theme.CoreTheme
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthenticationActivity : BaseActivity() {

    private val viewModel: AuthenticationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectAuthentication()
        initEventObservers()
        initStateObservers()
    }

    private fun initEventObservers() = repeatOnLifecycleStarted {
        viewModel.effect.collect { effect ->
            when (effect) {
                AuthenticationEffect.NavigateToNextStep -> this.finish()
            }
        }
    }

    private fun initStateObservers() = repeatOnLifecycleResumed {
        viewModel.uiState.collect { state ->
            renderState(state)
        }
    }

    private fun renderState(state: AuthenticationState) {
        setContent {
            CoreTheme {
                AuthenticationScreen(
                    modifier = Modifier.fillMaxWidth(),
                    authenticationState = state,
                    handleEvent = viewModel::handleEvent
                )
            }
        }
    }
}
