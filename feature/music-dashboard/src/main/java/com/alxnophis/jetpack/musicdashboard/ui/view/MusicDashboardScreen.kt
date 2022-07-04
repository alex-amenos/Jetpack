package com.alxnophis.jetpack.musicdashboard.ui.view

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.alxnophis.jetpack.core.ui.theme.CoreTheme

@Composable
internal fun MusicDashboardScreen(
    navController: NavController,
) {
    CoreTheme {
        val activity: Activity? = (LocalContext.current as? Activity)
        BackHandler {
            if (!navController.popBackStack()) {
                activity?.finish()
            }
        }
        MusicDashboard()
    }
}

@Composable
private fun MusicDashboard(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Text(text = "MusicDashboard")
    }
}
