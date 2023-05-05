package com.alxnophis.jetpack.game.ballclicker.di

import com.alxnophis.jetpack.game.ballclicker.ui.contract.BallClickerState
import com.alxnophis.jetpack.game.ballclicker.ui.viewmodel.BallClickerViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

internal fun injectBallClicker() = loadBallClickerModule

private val loadBallClickerModule by lazy {
    loadKoinModules(ballClickerModule)
}

private val ballClickerModule: Module = module {
    viewModel {
        BallClickerViewModel(
            initialState = BallClickerState(),
            defaultDispatcher = Dispatchers.Default
        )
    }
}
