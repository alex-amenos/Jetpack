package com.alxnophis.jetpack.location.tracker.di

import android.content.Context
import android.location.LocationManager
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
import com.alxnophis.jetpack.location.tracker.ui.viewmodel.LocationTrackerViewModel
import com.google.android.gms.location.LocationServices
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
        val context = androidContext()
        LocationDataSourceImpl(
            fusedLocationProvider = LocationServices.getFusedLocationProviderClient(context),
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        )
    }
    factory<LocationRepository> { LocationRepositoryImpl(get()) }
    factory { LocationAvailableUseCase(get()) }
    factory { LocationFlowUseCase(get()) }
    factory { LastKnownLocationFlowUseCase(get()) }
    factory { ProvideLastKnownLocationUseCase(get()) }
    factory { StartLocationProviderUseCase(get()) }
    factory { StopLocationProviderUseCase(get()) }
    viewModel { LocationTrackerViewModel(get(), get(), get(), get()) }
}
