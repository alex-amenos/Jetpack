package com.alxnophis.jetpack.radio.di

import com.alxnophis.jetpack.radio.data.repository.RadioStationsRepositoryImpl
import com.alxnophis.jetpack.radio.domain.repository.RadioStationsRepository
import com.alxnophis.jetpack.radio.domain.usecase.GetSpanishStationsUseCase
import com.alxnophis.jetpack.radio.playback.RadioPlayer
import com.alxnophis.jetpack.radio.playback.RadioPlayerController
import com.alxnophis.jetpack.radio.ui.viewmodel.RadioPlayerViewModel
import com.alxnophis.jetpack.radio.ui.viewmodel.RadioStationListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val radioModule: Module =
    module {
        single { RadioPlayerController(androidContext()) } bind RadioPlayer::class
        single<RadioStationsRepository> { RadioStationsRepositoryImpl(get()) }
        factory { GetSpanishStationsUseCase(get()) }

        viewModel { RadioStationListViewModel(get(), get()) }
        viewModel { RadioPlayerViewModel(get()) }
    }
