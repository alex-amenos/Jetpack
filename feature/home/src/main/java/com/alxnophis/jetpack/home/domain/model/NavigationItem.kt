package com.alxnophis.jetpack.home.domain.model

import android.content.Intent

data class NavigationItem(
    val name: String,
    val description: String,
    val intent: Intent?
)
