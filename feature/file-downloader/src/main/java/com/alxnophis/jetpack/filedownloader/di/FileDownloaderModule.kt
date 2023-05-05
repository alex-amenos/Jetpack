package com.alxnophis.jetpack.filedownloader.di

import com.alxnophis.jetpack.filedownloader.data.datasource.AndroidDownloaderDataSourceImpl
import com.alxnophis.jetpack.filedownloader.data.datasource.DownloaderDataSource
import com.alxnophis.jetpack.filedownloader.data.repository.FileDownloaderRepository
import com.alxnophis.jetpack.filedownloader.data.repository.FileDownloaderRepositoryImpl
import com.alxnophis.jetpack.filedownloader.ui.contract.FileDownloaderState
import com.alxnophis.jetpack.filedownloader.ui.viewmodel.FileDownloaderViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

fun injectFileDownloader() = loadFileDownloaderModules

private val loadFileDownloaderModules by lazy {
    loadKoinModules(fileDownloaderModule)
}

internal val fileDownloaderModule: Module = module {
    factory<DownloaderDataSource> {
        AndroidDownloaderDataSourceImpl(
            context = androidContext(),
            dateFormatter = get()
        )
    }
    single<FileDownloaderRepository> { FileDownloaderRepositoryImpl(androidDownloaderDataSource = get()) }
    viewModel {
        FileDownloaderViewModel(
            initialState = FileDownloaderState(),
            fileDownloaderRepository = get()
        )
    }
}
