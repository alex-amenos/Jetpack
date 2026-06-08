package com.alxnophis.jetpack.game.ballclicker.di

import com.alxnophis.jetpack.game.ballclicker.domain.usecase.BallClickerTimerUseCase
import com.alxnophis.jetpack.game.ballclicker.ui.viewmodel.BallClickerViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val ballClickerModule: Module =
    module {
        factoryOf(::BallClickerTimerUseCase)
        viewModel { BallClickerViewModel(get()) }
    }
