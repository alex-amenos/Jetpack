package com.alxnophis.jetpack.root.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.alxnophis.jetpack.root.di.injectRoot
import com.alxnophis.jetpack.root.ui.navigation.SetupNavGraph
import com.alxnophis.jetpack.root.ui.viewmodel.RootViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RootActivity : ComponentActivity() {

    private val viewModel: RootViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectRoot()
        renderContent()
    }

    private fun renderContent() {
        setContent {
            SetupNavGraph()
        }
    }
}
