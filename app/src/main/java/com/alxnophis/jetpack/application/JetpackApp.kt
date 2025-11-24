package com.alxnophis.jetpack.application

import com.alxnophis.jetpack.core.base.application.BaseApp
import com.alxnophis.jetpack.root.di.featureModules
import org.koin.core.module.Module

class JetpackApp : BaseApp() {
    override fun getFeatureModules(): List<Module> = featureModules
}
