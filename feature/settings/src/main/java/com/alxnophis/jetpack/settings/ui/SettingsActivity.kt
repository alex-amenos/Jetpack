package com.alxnophis.jetpack.settings.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alxnophis.jetpack.core.extensions.repeatOnLifecycleResumed
import com.alxnophis.jetpack.settings.di.injectSettings
import com.alxnophis.jetpack.settings.ui.view.SettingsComposable
import com.alxnophis.jetpack.settings.ui.viewmodel.SettingsViewModel
import timber.log.Timber

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectSettings()
        initSideEffectObserver()
        renderContent()
    }

    private fun initSideEffectObserver() = repeatOnLifecycleResumed {
        viewModel.sideEffect.collect { effect ->
            Timber.d("Settings side effect: $effect")
        }
    }

    private fun renderContent() {
        setContent {
            SettingsComposable()
        }
    }
}
