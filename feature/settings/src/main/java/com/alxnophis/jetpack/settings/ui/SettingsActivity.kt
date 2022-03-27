package com.alxnophis.jetpack.settings.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.alxnophis.jetpack.core.base.activity.BaseActivity
import com.alxnophis.jetpack.core.extensions.repeatOnLifecycleResumed
import com.alxnophis.jetpack.settings.di.injectSettings
import com.alxnophis.jetpack.settings.ui.contract.SettingsSideEffect
import com.alxnophis.jetpack.settings.ui.view.SettingsComposable
import com.alxnophis.jetpack.settings.ui.viewmodel.SettingsViewModel

class SettingsActivity : BaseActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectSettings()
        initSideEffectObserver()
        renderContent()
    }

    private fun initSideEffectObserver() = repeatOnLifecycleResumed {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is SettingsSideEffect.Finish -> this.finish()
            }
        }
    }

    private fun renderContent() {
        setContent {
            SettingsComposable()
        }
    }
}
