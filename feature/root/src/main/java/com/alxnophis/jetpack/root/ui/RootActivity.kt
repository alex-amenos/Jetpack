package com.alxnophis.jetpack.root.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.alxnophis.jetpack.root.ui.navigation.SetupNavGraph

class RootActivity : AppCompatActivity() {
    private lateinit var navigationController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val restoredState =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                savedInstanceState?.getParcelable(NAVIGATION_STATE, Bundle::class.java)
            } else {
                @Suppress("DEPRECATION")
                savedInstanceState?.getParcelable(NAVIGATION_STATE)
            }
        enableEdgeToEdge()
        setContent {
            navigationController = rememberNavController()
            navigationController.restoreState(restoredState)
            SetupNavGraph(navHostController = navigationController)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(NAVIGATION_STATE, navigationController.saveState())
    }

    companion object {
        private const val NAVIGATION_STATE = "nav_state"
    }
}
