package com.alxnophis.jetpack.root.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.alxnophis.jetpack.root.ui.navigation.SetupNavGraph

class RootActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        renderContent()
    }

    private fun renderContent() {
        setContent {
            SetupNavGraph()
        }
    }
}
