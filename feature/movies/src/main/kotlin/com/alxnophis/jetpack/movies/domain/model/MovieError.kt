package com.alxnophis.jetpack.movies.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface MovieError : Parcelable {
    data object Network : MovieError

    data object NotFound : MovieError

    data object Unauthorized : MovieError

    data class Unknown(
        val message: String?,
    ) : MovieError
}
