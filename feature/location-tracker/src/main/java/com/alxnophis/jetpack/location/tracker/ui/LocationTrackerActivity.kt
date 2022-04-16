package com.alxnophis.jetpack.location.tracker.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import com.alxnophis.jetpack.core.base.activity.BaseActivity
import com.alxnophis.jetpack.core.extensions.repeatOnLifecycleResumed
import com.alxnophis.jetpack.location.tracker.di.injectLocationTracker
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerEffect
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerEvent
import com.alxnophis.jetpack.location.tracker.ui.view.LocationTrackerScreen
import com.alxnophis.jetpack.location.tracker.ui.viewmodel.LocationTrackerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocationTrackerActivity : BaseActivity() {

    private val viewModel: LocationTrackerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectLocationTracker()
        initSideEffectObserver()
        renderContent()
    }

    override fun onResume() {
        super.onResume()
        viewModel.setEvent(LocationTrackerEvent.StartTrackingUserLocation)
    }

    override fun onPause() {
        super.onPause()
        viewModel.setEvent(LocationTrackerEvent.StopTrackingUserLocation)
    }

    private fun initSideEffectObserver() = repeatOnLifecycleResumed {
        viewModel.effect.collect { effect ->
            when (effect) {
                LocationTrackerEffect.Finish -> this.finish()
            }
        }
    }

    private fun renderContent() {
        setContent {
            LocationTrackerScreen()
        }
    }
}
