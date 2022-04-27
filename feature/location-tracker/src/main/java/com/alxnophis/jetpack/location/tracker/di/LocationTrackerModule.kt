package com.alxnophis.jetpack.location.tracker.di

import com.alxnophis.jetpack.location.tracker.data.data.FusedLocationDataSource
import com.alxnophis.jetpack.location.tracker.data.data.FusedLocationDataSourceImpl
import com.alxnophis.jetpack.location.tracker.data.repository.LastKnownLocationRepository
import com.alxnophis.jetpack.location.tracker.data.repository.LastKnownLocationRepositoryImpl
import com.alxnophis.jetpack.location.tracker.domain.usecase.GetUserLocationsFlowUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.StartLastKnownLocationRequestUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.StopLastKnownLocationRequestUseCase
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
    factory<FusedLocationDataSource> { FusedLocationDataSourceImpl(androidContext()) }
    single<LastKnownLocationRepository> { LastKnownLocationRepositoryImpl(fusedLocationDataSource = get()) }
    factory { StartLastKnownLocationRequestUseCase(lastKnownLocationRepository = get()) }
    factory { StopLastKnownLocationRequestUseCase(lastKnownLocationRepository = get()) }
    factory { GetUserLocationsFlowUseCase(lastKnownLocationRepository = get()) }
    viewModel {
        LocationTrackerViewModel(
            startLastKnownLocationRequestUseCase = get(),
            stopLastKnownLocationRequestUseCase = get(),
            getUserLocationsFlowUseCase = get()
        )
    }
}
