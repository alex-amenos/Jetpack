package com.alxnophis.jetpack.movies.domain.model

import com.alxnophis.jetpack.kotlin.constants.EMPTY

object MovieDetailsMother {
    operator fun invoke(
        id: Int = 1,
        title: String = EMPTY,
        overview: String = EMPTY,
        posterPath: String? = null,
        backdropPath: String? = null,
        releaseDate: String? = null,
        voteAverage: Double? = null,
        runtime: Int? = null,
        status: String? = null,
        tagline: String? = null,
    ) = MovieDetails(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        runtime = runtime,
        status = status,
        tagline = tagline,
    )
}
