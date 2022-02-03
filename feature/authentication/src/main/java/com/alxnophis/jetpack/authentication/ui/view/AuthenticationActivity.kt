package com.alxnophis.jetpack.authentication.ui.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.ui.ExperimentalComposeUiApi
import com.alxnophis.jetpack.authentication.di.injectAuthentication
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.authentication.ui.viewmodel.AuthenticationViewModel
import com.alxnophis.jetpack.core.base.activity.BaseActivity
import com.alxnophis.jetpack.core.extensions.repeatOnLifecycleResumed
import com.alxnophis.jetpack.router.features.RouterAuthentication
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalComposeUiApi
class AuthenticationActivity : BaseActivity() {

    private val viewModel: AuthenticationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectAuthentication()
        initStateObservers()
    }

    private fun initStateObservers() = repeatOnLifecycleResumed {
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
                        handleEvent = viewModel::setEvent
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
