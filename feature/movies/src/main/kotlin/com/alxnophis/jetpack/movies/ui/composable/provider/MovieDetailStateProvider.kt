package com.alxnophis.jetpack.movies.ui.composable.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.alxnophis.jetpack.movies.domain.model.MovieDetails
import com.alxnophis.jetpack.movies.domain.model.MovieError
import com.alxnophis.jetpack.movies.ui.contract.MovieDetailState

@Suppress("ktlint:standard:max-line-length")
internal class MovieDetailStateProvider : PreviewParameterProvider<MovieDetailState> {
    override val values: Sequence<MovieDetailState> =
        sequenceOf(
            MovieDetailState(
                isLoading = false,
                movieId = 1,
                movie =
                    MovieDetails(
                        id = 1,
                        title = "The Matrix",
                        overview = "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.",
                        posterPath = "https://image.tmdb.org/t/p/w500/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg",
                        backdropPath = "https://image.tmdb.org/t/p/w500/7u3pxc0K1wx32IleAkLv78MKgrw.jpg",
                        releaseDate = "1999-03-31",
                        voteAverage = 8.7,
                        runtime = 136,
                        status = "Released",
                        tagline = "Welcome to the Real World.",
                    ),
                error = null,
            ),
            MovieDetailState(
                isLoading = false,
                movieId = 1,
                movie =
                    MovieDetails(
                        id = 1,
                        title = "Unknown Movie",
                        overview = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        posterPath = null,
                        backdropPath = null,
                        releaseDate = "2026-01-01",
                        voteAverage = 1.0,
                        runtime = 90,
                        status = "Released",
                        tagline = null,
                    ),
                error = null,
            ),
            MovieDetailState(
                isLoading = true,
                movieId = null,
                movie = null,
                error = null,
            ),
            MovieDetailState(
                isLoading = false,
                movieId = null,
                movie = null,
                error = MovieError.Network,
            ),
        )
}
