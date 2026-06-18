package com.alxnophis.jetpack.api.themoviedb.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieSearchResponseApiModel(
    @SerialName("page") val page: Int,
    @SerialName("results") val results: List<MovieApiModel>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int,
)