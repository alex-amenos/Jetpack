package com.alxnophis.jetpack.api.interceptor

import com.alxnophis.jetpack.api.exception.NoConnectivityException
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Application interceptor that handles cache reading when offline.
 *
 * Works in coordination with NetworkStatusInterceptor and ForceCacheInterceptor:
 * - NetworkStatusInterceptor adds X-No-Network header when offline
 * - CacheInterceptor (this) forces reading from stale cache when offline
 * - ForceCacheInterceptor (network interceptor) ensures responses are cached
 *
 * Strategy when offline:
 * - Forces ONLY_IF_CACHED to prevent network attempts
 * - Allows stale cache up to 7 days
 * - Throws NoConnectivityException if no cache available
 *
 * Strategy when online:
 * - Lets OkHttp handle cache normally (fresh cache preferred)
 * - Falls back to network if cache expired
 *
 * This provides:
 * - Fast responses when cache is available (online or offline)
 * - Fresh data from network when cache expires (online only)
 * - Offline support with stale cache fallback
 * - Clear error when offline without cache
 */
internal class CacheInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Check if NetworkStatusInterceptor detected no network
        val isOffline = request.header(NetworkStatusInterceptor.HEADER_NO_NETWORK) == "true"

        if (isOffline) {
            Timber.d("CacheInterceptor: Device offline, forcing stale cache only")
            
            // Offline - force reading from cache only (even if stale)
            val staleCacheControl = CacheControl
                .Builder()
                .onlyIfCached() // Critical: Don't attempt network
                .maxStale(CACHE_MAX_STALE_DAYS, TimeUnit.DAYS) // Accept old cache
                .build()

            val cacheRequest = request
                .newBuilder()
                .cacheControl(staleCacheControl)
                .removeHeader(NetworkStatusInterceptor.HEADER_NO_NETWORK) // Clean header
                .build()

            val response = chain.proceed(cacheRequest)
            
            // Check if we got a valid cached response
            if (response.code == 504) { // Gateway Timeout = no cache available
                Timber.e("CacheInterceptor: No cache available while offline")
                throw NoConnectivityException()
            }
            
            Timber.d("CacheInterceptor: Serving from stale cache (age: ${response.cacheResponse?.sentRequestAtMillis})")
            return response
        }

        // Online - let OkHttp handle cache normally
        Timber.d("CacheInterceptor: Device online, using standard cache strategy")
        return chain.proceed(request)
    }

    private companion object {
        private const val CACHE_MAX_STALE_DAYS = 7 // Accept stale cache up to 7 days when offline
    }
}
