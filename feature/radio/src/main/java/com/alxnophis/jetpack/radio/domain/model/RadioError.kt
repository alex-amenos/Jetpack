package com.alxnophis.jetpack.radio.domain.model

internal sealed interface RadioError {
    data object NetworkUnavailable : RadioError

    data object ServerError : RadioError
}
