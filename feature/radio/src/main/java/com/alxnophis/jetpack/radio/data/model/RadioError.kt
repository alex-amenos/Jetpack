package com.alxnophis.jetpack.radio.data.model

internal sealed interface RadioError {
    data object NetworkUnavailable : RadioError

    data object ServerError : RadioError
}
