package com.alxnophis.jetpack.location.tracker.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.alxnophis.jetpack.core.ui.composable.CoreTopBar
import com.alxnophis.jetpack.core.ui.theme.CoreTheme
import com.alxnophis.jetpack.location.tracker.R

@Composable
internal fun LocationTrackerScreen() {
    CoreTheme {
        LocationTracker()
    }
}

@Composable
internal fun LocationTracker() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CoreTopBar(
            title = stringResource(id = R.string.location_tracker_title),
            onBack = {
                // TODO: navigate back
            }
        )
    }
}
