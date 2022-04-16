package com.alxnophis.jetpack.location.tracker.ui.viewmodel

import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerEffect
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerEvent
import com.alxnophis.jetpack.location.tracker.ui.contract.LocationTrackerState

internal class LocationTrackerViewModel(
    initialState: LocationTrackerState = LocationTrackerState()
) : BaseViewModel<LocationTrackerEvent, LocationTrackerState, LocationTrackerEffect>(initialState) {

    override fun handleEvent(event: LocationTrackerEvent) {
        when (event) {
            LocationTrackerEvent.Finish -> setEffect { LocationTrackerEffect.Finish }
        }
    }
}
