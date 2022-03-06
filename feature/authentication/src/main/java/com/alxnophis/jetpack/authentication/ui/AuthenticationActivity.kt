package com.alxnophis.jetpack.authentication.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.ui.ExperimentalComposeUiApi
import com.alxnophis.jetpack.authentication.di.injectAuthentication
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.authentication.ui.view.AuthenticationScreen
import com.alxnophis.jetpack.authentication.ui.viewmodel.AuthenticationViewModel
import com.alxnophis.jetpack.core.base.activity.BaseActivity
import com.alxnophis.jetpack.core.extensions.repeatOnLifecycleResumed
import com.alxnophis.jetpack.core.extensions.repeatOnLifecycleStarted
import com.alxnophis.jetpack.router.features.RouterAuthentication
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

@ExperimentalComposeUiApi
class AuthenticationActivity : BaseActivity() {

    private val viewModel: AuthenticationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectAuthentication()
        initEffectObserver()
        initStateObserver()
    }

    private fun initEffectObserver() = repeatOnLifecycleResumed {
        viewModel.effect.collect { effect ->
            Timber.d("Authentication effect: $effect")
        }
    }

    private fun initStateObserver() = repeatOnLifecycleStarted {
        viewModel.uiState.collect { state ->
            renderState(state)
        }
    }

    private fun renderState(state: AuthenticationState) {
        setContent {
            when {
                state.isUserAuthorized -> {
                    this.finish()
                    navigateToAuthorizedScreen()
                }
                else -> {
                    AuthenticationScreen(
                        authenticationState = state,
                        handleEvent = viewModel::setAction
                    )
                }
            }
        }
    }

    private fun navigateToAuthorizedScreen() {
        RouterAuthentication.activityAuthorized?.let {
            startActivity(it)
        }
    }
}
