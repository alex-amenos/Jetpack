package com.alxnophis.jetpack.home.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.alxnophis.jetpack.core.base.activity.BaseActivity
import com.alxnophis.jetpack.core.extensions.repeatOnLifecycleResumed
import com.alxnophis.jetpack.home.di.injectHome
import com.alxnophis.jetpack.home.ui.contract.HomeEffect
import com.alxnophis.jetpack.home.ui.view.HomeScreen
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
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateTo -> navigateTo(effect.intent)
            }
        }
    }

    private fun renderContent() {
        setContent {
            HomeScreen()
        }
    }

    private fun navigateTo(intent: Intent?) {
        startActivity(intent)
    }
}
