package com.alxnophis.jetpack.movies.ui.composable.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import com.alxnophis.jetpack.kotlin.constants.EMPTY
import com.alxnophis.jetpack.movies.domain.model.Movie
import com.alxnophis.jetpack.movies.domain.model.MovieError
import com.alxnophis.jetpack.movies.domain.model.MovieException
import com.alxnophis.jetpack.movies.ui.contract.MoviesState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class MoviesStateProvider : PreviewParameterProvider<MoviesState> {
    override val values: Sequence<MoviesState> =
        sequenceOf(
            MoviesState(
                searchQuery = EMPTY,
            ),
            MoviesState(
                searchQuery = "Matrix",
            ),
        )
}

@Suppress("ktlint:standard:max-line-length")
internal class MoviesPagingProvider : PreviewParameterProvider<Flow<PagingData<Movie>>> {
    override val values: Sequence<Flow<PagingData<Movie>>> =
        sequenceOf(
            flowOf(
                PagingData.from(
                    listOf(
                        Movie(
                            id = 1,
                            title = "The Matrix",
                            overview = "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.",
                            posterPath = null,
                            backdropPath = null,
                            releaseDate = "1999-03-31",
                            voteAverage = 8.7,
                        ),
                        Movie(
                            id = 2,
                            title = "The Matrix Reloaded",
                            overview = "Neo and the rebel leaders estimate that they have 72 hours until 250,000 probes discover Zion and destroy it and its inhabitants.",
                            posterPath = null,
                            backdropPath = null,
                            releaseDate = "2003-05-15",
                            voteAverage = 7.0,
                        ),
                        Movie(
                            id = 3,
                            title = "The Matrix Revolutions",
                            overview = "The human city of Zion defends itself against the massive invasion of the machines as Neo fights to end the war at another front while also opposing the rogue Agent Smith.",
                            posterPath = null,
                            backdropPath = null,
                            releaseDate = "2003-11-05",
                            voteAverage = 6.7,
                        ),
                    ),
                ),
            ),
            flowOf(
                PagingData.from(
                    data = emptyList(),
                    sourceLoadStates =
                        LoadStates(
                            refresh = LoadState.NotLoading(endOfPaginationReached = true),
                            prepend = LoadState.NotLoading(endOfPaginationReached = true),
                            append = LoadState.NotLoading(endOfPaginationReached = true),
                        ),
                ),
            ),
            flowOf(
                PagingData.from(
                    data = emptyList(),
                    sourceLoadStates =
                        LoadStates(
                            refresh = LoadState.Error(MovieException(MovieError.Network)),
                            prepend = LoadState.NotLoading(endOfPaginationReached = true),
                            append = LoadState.NotLoading(endOfPaginationReached = true),
                        ),
                ),
            ),
        )
}
