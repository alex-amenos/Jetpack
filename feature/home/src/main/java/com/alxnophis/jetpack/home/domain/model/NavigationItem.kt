package com.alxnophis.jetpack.home.domain.model

import com.alxnophis.jetpack.router.screen.Screen
import java.util.UUID

data class NavigationItem(
    val id: Long = UUID.randomUUID().mostSignificantBits,
    val name: String,
    val description: String,
    val screen: Screen
)
