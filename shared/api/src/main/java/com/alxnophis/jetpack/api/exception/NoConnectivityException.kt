package com.alxnophis.jetpack.api.exception

import java.io.IOException

/**
 * Exception thrown when there is no network connectivity available.
 *
 * This exception is thrown by [com.alxnophis.jetpack.api.interceptor.NetworkStatusInterceptor]
 * when attempting to make a network request without an active internet connection.
 *
 * Extends [IOException] to be compatible with Arrow's CallError mapping, which treats
 * IOExceptions as [arrow.retrofit.adapter.either.networkhandling.IOError].
 */
class NoConnectivityException : IOException("No network connectivity available")
