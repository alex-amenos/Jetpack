package com.alxnophis.jetpack.game.ballclicker.di

import com.alxnophis.jetpack.game.ballclicker.ui.viewmodel.BallClickerViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val ballClickerModule: Module =
    module {
        viewModel { BallClickerViewModel() }
    }
