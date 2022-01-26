package com.alxnophis.jetpack.authentication.di

import com.alxnophis.jetpack.authentication.domain.usecase.UseCaseAuthenticate
import com.alxnophis.jetpack.authentication.ui.viewmodel.AuthenticationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

fun injectAuthentication() = loadAuthenticationModules

private val loadAuthenticationModules by lazy {
    loadKoinModules(
        listOf(
            authenticationModule
        )
    )
}

private val authenticationModule: Module = module {
    factory { UseCaseAuthenticate() }
    viewModel {
        AuthenticationViewModel(useCaseAuthenticate = get())
    }
}
