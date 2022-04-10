package com.alxnophis.jetpack.authentication.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.ui.ExperimentalComposeUiApi
import com.alxnophis.jetpack.authentication.di.injectAuthentication
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEffect
import com.alxnophis.jetpack.authentication.ui.view.AuthenticationScreen
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
        initSideEffectObserver()
        renderContent()
    }

    private fun initSideEffectObserver() = repeatOnLifecycleResumed {
        viewModel.effect.collect { effect ->
            when (effect) {
                AuthenticationEffect.UserAuthorized -> {
                    this.finish()
                    navigateToAuthorizedScreen()
                }
            }
        }
    }

    private fun renderContent() {
        setContent {
            AuthenticationScreen()
        }
    }

    private fun navigateToAuthorizedScreen() {
        RouterAuthentication.activityAuthorized?.let {
            startActivity(it)
        }
    }
}
