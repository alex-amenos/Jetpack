package com.alxnophis.jetpack.movies.domain.model

import com.alxnophis.jetpack.kotlin.constants.EMPTY

object MovieMother {
    operator fun invoke(
        id: Int = 1,
        title: String = EMPTY,
        overview: String = EMPTY,
        posterPath: String? = null,
        backdropPath: String? = null,
        releaseDate: String? = null,
        voteAverage: Double? = null,
    ) = Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
    )
}
