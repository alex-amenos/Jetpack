package com.alxnophis.jetpack.home.domain.model

import com.alxnophis.jetpack.router.screen.Screen

data class NavigationItem(
    val name: String,
    val description: String,
    val screen: Screen
)
