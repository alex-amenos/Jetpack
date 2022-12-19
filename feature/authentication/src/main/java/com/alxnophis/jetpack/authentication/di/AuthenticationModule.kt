package com.alxnophis.jetpack.authentication.di

import com.alxnophis.jetpack.authentication.domain.usecase.AuthenticateUseCase
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationState
import com.alxnophis.jetpack.authentication.ui.viewmodel.AuthenticationViewModel
import com.alxnophis.jetpack.kotlin.utils.DefaultDispatcherProvider
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

fun injectAuthentication() = loadAuthenticationModules

private val loadAuthenticationModules by lazy {
    loadKoinModules(authenticationModule)
}

private val authenticationModule: Module = module {
    factory { AuthenticateUseCase(dispatchers = get()) }
    factory<DispatcherProvider> { DefaultDispatcherProvider() }
    viewModel {
        AuthenticationViewModel(
            initialState = AuthenticationState(),
            dispatchers = get(),
            authenticateUseCase = get(),
        )
    }
}
