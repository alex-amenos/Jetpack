package com.alxnophis.jetpack.game.ballclicker.di

import com.alxnophis.jetpack.game.ballclicker.ui.viewmodel.BallClickerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val ballClickerModule: Module =
    module {
        viewModel { BallClickerViewModel() }
    }
