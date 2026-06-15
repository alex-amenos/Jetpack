package com.alxnophis.jetpack.movies.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import arrow.retrofit.adapter.either.networkhandling.HttpError
import arrow.retrofit.adapter.either.networkhandling.IOError
import arrow.retrofit.adapter.either.networkhandling.UnexpectedCallError
import com.alxnophis.jetpack.api.themoviedb.TheMovieDbSearchService
import com.alxnophis.jetpack.movies.domain.model.Movie
import com.alxnophis.jetpack.movies.domain.model.MovieError
import com.alxnophis.jetpack.movies.domain.model.MovieException
import timber.log.Timber

internal class MoviePagingSource(
    private val searchService: TheMovieDbSearchService,
    private val query: String,
) : PagingSource<Int, Movie>() {
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1) ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        if (query.isBlank()) {
            return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
        }

        val page = params.key ?: 1

        return searchService
            .searchMovies(query = query, page = page)
            .fold(
                { callError ->
                    Timber.e("Error fetching movies: $callError. Query: $query, Page: $page")
                    val error =
                        when (callError) {
                            is IOError -> {
                                MovieError.Network
                            }

                            is HttpError -> {
                                when (callError.code) {
                                    401 -> MovieError.Unauthorized
                                    404 -> MovieError.NotFound
                                    else -> MovieError.Unknown(callError.message)
                                }
                            }

                            is UnexpectedCallError -> {
                                MovieError.Unknown(callError.cause.message)
                            }
                        }
                    LoadResult.Error(MovieException(error))
                },
                { response ->
                    val movies =
                        response.results.map {
                            Movie(
                                id = it.id,
                                title = it.title,
                                overview = it.overview,
                                posterPath = it.posterPath,
                                backdropPath = it.backdropPath,
                                releaseDate = it.releaseDate,
                                voteAverage = it.voteAverage,
                            )
                        }

                    LoadResult.Page(
                        data = movies,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (page < response.totalPages) page + 1 else null,
                    )
                },
            )
    }
}
