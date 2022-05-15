package com.alxnophis.jetpack.root.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.alxnophis.jetpack.core.base.activity.BaseActivity
import com.alxnophis.jetpack.root.di.injectRoot
import com.alxnophis.jetpack.root.navigation.SetupNavGraph
import com.alxnophis.jetpack.root.ui.viewmodel.RootViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RootActivity : BaseActivity() {

    private val viewModel: RootViewModel by viewModel()
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectRoot()
        lifecycle.addObserver(viewModel)
        renderContent()
    }

    override fun onDestroy() {
        lifecycle.removeObserver(viewModel)
        super.onDestroy()
    }

    private fun renderContent() {
        setContent {
            navController = rememberNavController()
            SetupNavGraph(navController)
        }
    }
}
