package com.alxnophis.jetpack.location.tracker.di

import com.alxnophis.jetpack.location.tracker.data.data.LocationDataSource
import com.alxnophis.jetpack.location.tracker.data.data.LocationDataSourceImpl
import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepository
import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepositoryImpl
import com.alxnophis.jetpack.location.tracker.domain.usecase.LastKnownLocationStateUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.LocationAvailableUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.LocationStateUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.ProvideLastKnownLocationUseCase
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
    single<LocationDataSource> { LocationDataSourceImpl(context = androidContext()) }
    factory<LocationRepository> { LocationRepositoryImpl(locationDataSource = get()) }
    factory { LocationAvailableUseCase(locationRepository = get()) }
    factory { LocationStateUseCase(locationRepository = get()) }
    factory { LastKnownLocationStateUseCase(locationRepository = get()) }
    factory { ProvideLastKnownLocationUseCase(locationRepository = get()) }
    factory { StartLocationRequestUseCase(locationRepository = get()) }
    factory { StopLocationRequestUseCase(locationRepository = get()) }
    viewModel {
        LocationTrackerViewModel(
            startLocationRequestUseCase = get(),
            stopLocationRequestUseCase = get(),
            locationStateUseCase = get()
        )
    }
}
