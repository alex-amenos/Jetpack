package com.alxnophis.jetpack.movies.ui.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.alxnophis.jetpack.movies.R
import com.alxnophis.jetpack.movies.domain.model.MovieError

@Composable
internal fun MovieError.toMessage(): String =
    when (this) {
        MovieError.Network -> stringResource(id = R.string.movies_error_network)
        MovieError.NotFound -> stringResource(id = R.string.movies_error_not_found)
        MovieError.Unauthorized -> stringResource(id = R.string.movies_error_unauthorized)
        is MovieError.Unknown -> {
            val msg = this.message
            if (!msg.isNullOrBlank()) {
                stringResource(id = R.string.movies_error_unknown_message, msg)
            } else {
                stringResource(id = R.string.movies_error_unknown)
            }
        }
    }
