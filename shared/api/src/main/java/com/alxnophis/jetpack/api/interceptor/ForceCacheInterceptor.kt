package com.alxnophis.jetpack.api.interceptor

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Network interceptor that forces OkHttp to cache responses.
 *
 * This interceptor rewrites response headers to ensure caching works even when
 * the server doesn't send appropriate Cache-Control headers.
 *
 * Why a Network Interceptor?
 * - Network interceptors can modify response headers after receiving from server
 * - Application interceptors can't modify response headers that OkHttp uses for caching
 *
 * Strategy:
 * - Rewrites Cache-Control header in responses to enable caching
 * - Sets max-age based on configuration
 * - Forces OkHttp to store responses in cache
 *
 * @param maxAgeMinutes How long responses should be cached (default: 15 minutes)
 */
internal class ForceCacheInterceptor(
    private val maxAgeMinutes: Int = DEFAULT_MAX_AGE_MINUTES,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        // Force cache by rewriting response headers
        val cacheControl = CacheControl
            .Builder()
            .maxAge(maxAgeMinutes, TimeUnit.MINUTES)
            .build()

        Timber.d("ForceCacheInterceptor: Forcing cache for ${maxAgeMinutes}min - ${response.request.url}")

        return response
            .newBuilder()
            .removeHeader(PRAGMA_HEADER) // Remove conflicting headers
            .removeHeader(CACHE_CONTROL_HEADER)
            .header("Cache-Control", cacheControl.toString())
            .build()
    }

    private companion object {
        private const val DEFAULT_MAX_AGE_MINUTES = 15
        private const val PRAGMA_HEADER = "Pragma"
        private const val CACHE_CONTROL_HEADER = "Cache-Control"
    }
}
