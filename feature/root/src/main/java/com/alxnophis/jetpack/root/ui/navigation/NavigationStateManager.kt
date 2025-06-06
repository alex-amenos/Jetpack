package com.alxnophis.jetpack.root.ui.navigation

import android.os.Bundle
import androidx.navigation.NavHostController

interface NavigationStateManager {
    fun saveNavigationState(
        outState: Bundle,
        navController: NavHostController,
    )

    fun restoreNavigationState(savedInstanceState: Bundle?): Bundle?

    fun applyNavigationState(
        navController: NavHostController,
        restoredState: Bundle?,
    )
}
