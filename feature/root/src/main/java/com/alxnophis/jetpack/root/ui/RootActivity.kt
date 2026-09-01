package com.alxnophis.jetpack.root.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.root.ui.navigation.Navigation

class RootActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Navigation(
                    modifier =
                        Modifier
                            .semantics { testTagsAsResourceId = true }
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                )
            }
        }
    }
}
