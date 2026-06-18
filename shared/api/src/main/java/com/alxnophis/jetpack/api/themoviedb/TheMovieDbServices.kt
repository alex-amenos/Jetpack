package com.alxnophis.jetpack.api.themoviedb

import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import com.alxnophis.jetpack.api.themoviedb.model.MovieDetailsApiModel
import com.alxnophis.jetpack.api.themoviedb.model.MovieSearchResponseApiModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDbSearchService {
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("language") language: String = "en-US",
    ): Either<CallError, MovieSearchResponseApiModel>
}

interface TheMovieDbMovieService {
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US",
    ): Either<CallError, MovieDetailsApiModel>
}