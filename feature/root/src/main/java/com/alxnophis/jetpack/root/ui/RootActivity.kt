package com.alxnophis.jetpack.root.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alxnophis.jetpack.core.extensions.testTagsAsResourceIdInDebug
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.root.ui.composable.OfflineIndicator
import com.alxnophis.jetpack.root.ui.navigation.Navigation
import com.alxnophis.jetpack.root.ui.viewmodel.RootViewModel
import com.alxnophis.jetpack.settings.data.model.SettingsPreferences.ThemeOptions
import org.koin.androidx.viewmodel.ext.android.viewModel

class RootActivity : ComponentActivity() {
    private val rootViewModel: RootViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val state by rootViewModel.uiState.collectAsStateWithLifecycle()
            val isDarkTheme =
                when (state.themeOption) {
                    ThemeOptions.SYSTEM -> isSystemInDarkTheme()
                    ThemeOptions.LIGHT -> false
                    ThemeOptions.DARK -> true
                }
            AppTheme(darkTheme = isDarkTheme) {
                Column(
                    modifier =
                        Modifier
                            .testTagsAsResourceIdInDebug()
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                ) {
                    OfflineIndicator(isOnline = state.isOnline)
                    Navigation(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),
                    )
                }
            }
        }
    }
}
