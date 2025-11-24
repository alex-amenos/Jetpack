package com.alxnophis.jetpack.filedownloader.di

import com.alxnophis.jetpack.filedownloader.data.datasource.AndroidDownloaderDataSourceImpl
import com.alxnophis.jetpack.filedownloader.data.datasource.DownloaderDataSource
import com.alxnophis.jetpack.filedownloader.data.repository.FileDownloaderRepository
import com.alxnophis.jetpack.filedownloader.data.repository.FileDownloaderRepositoryImpl
import com.alxnophis.jetpack.filedownloader.ui.viewmodel.FileDownloaderViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val fileDownloaderModule: Module =
    module {
        factory<DownloaderDataSource> { AndroidDownloaderDataSourceImpl(androidContext(), get()) }
        single<FileDownloaderRepository> { FileDownloaderRepositoryImpl(get()) }
        viewModel { FileDownloaderViewModel(get()) }
    }
