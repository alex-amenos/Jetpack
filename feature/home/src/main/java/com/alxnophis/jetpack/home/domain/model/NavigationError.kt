package com.alxnophis.jetpack.home.domain.model

sealed class NavigationError {
    data object Unknown : NavigationError()
}
