package com.alxnophis.jetpack.settings.ui.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alxnophis.jetpack.core.extensions.repeatOnLifecycleResumed
import com.alxnophis.jetpack.settings.di.injectSettings
import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.settings.ui.viewmodel.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectSettings()
        initEventObservers()
        initStateObservers()
    }

    private fun initEventObservers() = repeatOnLifecycleResumed {
        viewModel.effect.collect { _ ->
            // TODO
        }
    }

    private fun initStateObservers() = repeatOnLifecycleResumed {
        viewModel.uiState.collect { state ->
            renderState(state)
        }
    }

    private fun renderState(state: SettingsState) {
        setContent {
            SettingsScreen(
                settingsState = state,
                handleEvent = viewModel::setEvent
            )
        }
    }
}
