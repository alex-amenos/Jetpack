package com.alxnophis.jetpack.settings.di

import com.alxnophis.jetpack.settings.ui.contract.SettingsState
import com.alxnophis.jetpack.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

internal fun injectSettings() = loadSettingsModule

private val loadSettingsModule by lazy {
    loadKoinModules(
        listOf(
            settingsModule
        )
    )
}

private val settingsModule: Module = module {
    viewModel { SettingsViewModel(initialState = SettingsState()) }
}
