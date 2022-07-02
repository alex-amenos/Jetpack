package com.alxnophis.jetpack.location.tracker.di

import android.content.Context
import android.location.LocationManager
import com.alxnophis.jetpack.kotlin.utils.DefaultDispatcherProvider
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import com.alxnophis.jetpack.location.tracker.data.data.LocationDataSource
import com.alxnophis.jetpack.location.tracker.data.data.LocationDataSourceImpl
import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepository
import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepositoryImpl
import com.alxnophis.jetpack.location.tracker.domain.usecase.LastKnownLocationFlowUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.LocationAvailableUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.LocationFlowUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.ProvideLastKnownLocationUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.StartLocationProviderUseCase
import com.alxnophis.jetpack.location.tracker.domain.usecase.StopLocationProviderUseCase
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerState
import com.alxnophis.jetpack.location.tracker.ui.viewmodel.LocationTrackerViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableSharedFlow
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
    single<LocationDataSource> {
        LocationDataSourceImpl(
            dispatcherProvider = get(),
            fusedLocationProvider = LocationServices.getFusedLocationProviderClient(androidContext()),
            locationManager = androidContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager,
            mutableLocationSharedFlow = MutableSharedFlow(),
        )
    }
    factory<DispatcherProvider> { DefaultDispatcherProvider() }
    factory<LocationRepository> { LocationRepositoryImpl(locationDataSource = get()) }
    factory { LocationAvailableUseCase(locationRepository = get()) }
    factory { LocationFlowUseCase(locationRepository = get()) }
    factory { LastKnownLocationFlowUseCase(locationRepository = get()) }
    factory { ProvideLastKnownLocationUseCase(dispatchers = get(), locationRepository = get()) }
    factory { StartLocationProviderUseCase(locationRepository = get()) }
    factory { StopLocationProviderUseCase(locationRepository = get()) }
    viewModel {
        LocationTrackerViewModel(
            initialState = LocationTrackerState(),
            startLocationProviderUseCase = get(),
            stopLocationProviderUseCase = get(),
            locationStateUseCase = get(),
            lastKnownLocationUseCase = get()
        )
    }
}
