package com.alxnophis.jetpack.location.tracker.di

import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepository
import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepositoryImpl
import com.alxnophis.jetpack.location.tracker.domain.usecase.GetUserLocationsFlowUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.StartLocationRequestUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.StopLocationRequestUseCase
import com.alxnophis.jetpack.location.tracker.ui.viewmodel.LocationTrackerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

fun injectLocationTracker() = loadLocationTrackerModules

private val loadLocationTrackerModules by lazy {
    loadKoinModules(
        listOf(
            locationTrackerModule
        )
    )
}

private val locationTrackerModule: Module = module {
    single<LocationRepository> { LocationRepositoryImpl(context = androidContext()) }
    factory { StartLocationRequestUseCase(locationRepository = get()) }
    factory { StopLocationRequestUseCase(locationRepository = get()) }
    factory { GetUserLocationsFlowUseCase(locationRepository = get()) }
    viewModel {
        LocationTrackerViewModel(
            startLocationRequestUseCase = get(),
            stopLocationRequestUseCase = get(),
            getUserLocationsFlowUseCase = get()
        )
    }
}
