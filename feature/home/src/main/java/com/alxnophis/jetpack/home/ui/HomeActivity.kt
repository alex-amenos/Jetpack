package com.alxnophis.jetpack.home.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.alxnophis.jetpack.core.base.activity.BaseActivity
import com.alxnophis.jetpack.core.extensions.repeatOnLifecycleResumed
import com.alxnophis.jetpack.home.di.injectHome
import com.alxnophis.jetpack.home.ui.contract.HomeSideEffect
import com.alxnophis.jetpack.home.ui.view.HomeComposable
import com.alxnophis.jetpack.home.ui.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : BaseActivity() {

    private val viewModel: HomeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectHome()
        initSideEffectObserver()
        renderContent()
    }

    private fun initSideEffectObserver() = repeatOnLifecycleResumed {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is HomeSideEffect.NavigateTo -> navigateTo(sideEffect.intent)
            }
        }
    }

    private fun renderContent() {
        setContent {
            HomeComposable()
        }
    }

    private fun navigateTo(intent: Intent?) {
        startActivity(intent)
    }
}
