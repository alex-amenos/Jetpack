package com.alxnophis.jetpack.location.tracker.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import com.alxnophis.jetpack.core.base.activity.BaseActivity
import com.alxnophis.jetpack.location.tracker.ui.view.LocationTrackerScreen

class LocationTrackerActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        renderContent()
    }

    private fun renderContent(){
        setContent {
            LocationTrackerScreen()
        }
    }
}
