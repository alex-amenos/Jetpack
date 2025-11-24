package com.alxnophis.jetpack.authentication.di

import com.alxnophis.jetpack.authentication.domain.usecase.AuthenticateUseCase
import com.alxnophis.jetpack.authentication.ui.viewmodel.AuthenticationViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authenticationModule: Module =
    module {
        factory { AuthenticateUseCase() }
        viewModel { AuthenticationViewModel(get()) }
    }
