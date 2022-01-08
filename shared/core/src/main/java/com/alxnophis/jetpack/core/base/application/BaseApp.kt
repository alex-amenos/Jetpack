package com.alxnophis.jetpack.core.base.application

import android.app.Application
import android.os.StrictMode
import com.alxnophis.jetpack.core.BuildConfig
import com.alxnophis.jetpack.core.di.KoinLogger
import com.alxnophis.jetpack.core.extensions.isDebugBuildType
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

open class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin()
        initLogger()
        initStrictMode()
    }

    private fun initKoin() {
        val koinApp = startKoin { androidContext(this@BaseApp) }
        if (BuildConfig.DEBUG) {
            koinApp.logger(KoinLogger())
        }
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initStrictMode() {
        if (isDebugBuildType()) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork() // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
//                .penaltyDeath()
                    .build()
            )
        }
    }
}
