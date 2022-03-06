package com.alxnophis.jetpack.settings.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alxnophis.jetpack.core.extensions.repeatOnLifecycleResumed
import com.alxnophis.jetpack.core.extensions.repeatOnLifecycleStarted
import com.alxnophis.jetpack.settings.di.injectSettings
import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.settings.ui.view.SettingsScreen
import com.alxnophis.jetpack.settings.ui.viewmodel.SettingsViewModel
import timber.log.Timber

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectSettings()
        initEffectObserver()
        initStateObserver()
    }

    private fun initEffectObserver() = repeatOnLifecycleResumed {
        viewModel.effect.collect { effect ->
            Timber.d("Settings effect: $effect")
        }
    }

    private fun initStateObserver() = repeatOnLifecycleStarted {
        viewModel.uiState.collect { state ->
            renderState(state)
        }
    }

    private fun renderState(state: SettingsState) {
        setContent {
            SettingsScreen(
                settingsState = state,
                handleEvent = viewModel::setAction
            )
        }
    }
}
