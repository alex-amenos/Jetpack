package com.alxnophis.jetpack.root.ui.navigation

import android.os.Build
import android.os.Bundle
import androidx.navigation.NavHostController

class NavigationStateManagerImpl : NavigationStateManager {
    override fun saveNavigationState(
        outState: Bundle,
        navController: NavHostController,
    ) {
        outState.putParcelable(NAVIGATION_STATE, navController.saveState())
    }

    override fun restoreNavigationState(savedInstanceState: Bundle?): Bundle? =
        if (savedInstanceState == null) {
            null
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            savedInstanceState.getParcelable(NAVIGATION_STATE, Bundle::class.java)
        } else {
            @Suppress("DEPRECATION")
            savedInstanceState.getParcelable(NAVIGATION_STATE)
        }

    override fun applyNavigationState(
        navController: NavHostController,
        restoredState: Bundle?,
    ) {
        navController.restoreState(restoredState)
    }

    companion object {
        private const val NAVIGATION_STATE = "nav_state"
    }
}
