package com.alxnophis.jetpack.root.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.alxnophis.jetpack.root.ui.navigation.NavigationStateManager
import com.alxnophis.jetpack.root.ui.navigation.NavigationStateManagerImpl
import com.alxnophis.jetpack.root.ui.navigation.SetupNavGraph

class RootActivity : AppCompatActivity() {
    private val navigationStateManager: NavigationStateManager = NavigationStateManagerImpl()

    private lateinit var navigationController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val restoredState = navigationStateManager.restoreNavigationState(savedInstanceState)
        setContent {
            navigationController = rememberNavController()
            navigationStateManager.applyNavigationState(navigationController, restoredState)
            SetupNavGraph(navHostController = navigationController)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        navigationStateManager.saveNavigationState(outState, navigationController)
    }
}
