package com.alxnophis.jetpack.movies.domain.model

class MovieException(val error: MovieError) : Exception(error.toString())
