package com.alxnophis.jetpack.settings.di

import com.alxnophis.jetpack.settings.data.repository.SettingsRepository
import com.alxnophis.jetpack.settings.data.repository.SettingsRepositoryImpl
import com.alxnophis.jetpack.settings.ui.viewmodel.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val settingsModule: Module =
    module {
        single<SettingsRepository> { SettingsRepositoryImpl(androidContext()) }
        viewModel { SettingsViewModel(get()) }
    }
