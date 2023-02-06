package com.alxnophis.jetpack.filedownloader.di

import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

fun injectFileDownloader() = loadFileDownloaderModules

private val loadFileDownloaderModules by lazy {
    loadKoinModules(fileDownloaderModule)
}

private val fileDownloaderModule: Module = module {
}
