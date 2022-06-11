package com.alxnophis.jetpack.game.ballclicker.di

import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

internal fun injectBallClicker() = loadBallClickerModule

private val loadBallClickerModule by lazy {
    loadKoinModules(
        listOf(
            ballClickerModule
        )
    )
}

private val ballClickerModule: Module = module {
    //viewModel { }
}
