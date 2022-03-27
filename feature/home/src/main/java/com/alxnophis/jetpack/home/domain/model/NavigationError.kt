package com.alxnophis.jetpack.home.domain.model

sealed class NavigationError {
    object Unknown : NavigationError()
}
