package com.alxnophis.jetpack.location.tracker.di

import android.content.Context
import android.location.LocationManager
import com.alxnophis.jetpack.location.tracker.data.data.LocationDataSource
import com.alxnophis.jetpack.location.tracker.data.data.LocationDataSourceImpl
import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepository
import com.alxnophis.jetpack.location.tracker.data.repository.LocationRepositoryImpl
import com.alxnophis.jetpack.location.tracker.ui.viewmodel.LocationTrackerViewModel
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val locationTrackerModule: Module =
    module {
        single<LocationDataSource> {
            val context = androidContext()
            LocationDataSourceImpl(
                fusedLocationProvider = LocationServices.getFusedLocationProviderClient(context),
                locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager,
            )
        }
        factory<LocationRepository> { LocationRepositoryImpl(get()) }
        viewModel { LocationTrackerViewModel(get()) }
    }
