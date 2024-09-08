package com.alxnophis.jetpack.home.domain.model

import com.alxnophis.jetpack.router.screen.Route
import java.util.UUID

data class NavigationItem(
    val id: Long = UUID.randomUUID().mostSignificantBits,
    val emoji: String,
    val name: String,
    val description: String,
    val route: Route,
)
