package com.alxnophis.jetpack.filedownloader.di

import com.alxnophis.jetpack.filedownloader.domain.AndroidDownloader
import com.alxnophis.jetpack.filedownloader.domain.AndroidDownloaderImpl
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

private val fileDownloaderModule: Module = module {
    factory<AndroidDownloader> {
        AndroidDownloaderImpl(
            context = androidContext(),
            dateFormatter = get()
        )
    }
    viewModel {
        FileDownloaderViewModel(
            initialState = FileDownloaderState(),
            downloader = get()
        )
    }
}
