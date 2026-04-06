package com.alxnophis.jetpack.api.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import kotlin.math.pow

/**
 * Interceptor that retries failed requests due to transient errors.
 *
 * Retries on:
 * - IOException (network errors, timeouts)
 * - HTTP 5xx (server errors)
 * - HTTP 429 (too many requests)
 *
 * Does NOT retry on:
 * - HTTP 4xx (client errors, except 429)
 * - Successful responses
 *
 * @param maxRetries Maximum number of retry attempts (default: 3)
 * @param initialDelayMs Initial delay between retries in milliseconds (default: 1000)
 * @param maxDelayMs Maximum delay between retries in milliseconds (default: 10000)
 * @param backoffMultiplier Multiplier for exponential backoff (default: 2.0)
 */
class RetryInterceptor(
    private val maxRetries: Int = DEFAULT_MAX_RETRIES,
    private val initialDelayMs: Long = DEFAULT_INITIAL_DELAY_MS,
    private val maxDelayMs: Long = DEFAULT_MAX_DELAY_MS,
    private val backoffMultiplier: Double = DEFAULT_BACKOFF_MULTIPLIER,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var lastException: IOException? = null
        var lastResponse: Response? = null

        repeat(maxRetries + 1) { attempt ->
            try {
                lastResponse?.close()
                val response = chain.proceed(request)

                if (response.isSuccessful || !shouldRetry(response.code)) {
                    return response
                }

                lastResponse = response
                Timber.w("Retry attempt ${attempt + 1}/${maxRetries + 1} for ${request.url} - HTTP ${response.code}")
            } catch (e: IOException) {
                lastException = e
                Timber.w(e, "Retry attempt ${attempt + 1}/${maxRetries + 1} for ${request.url}")
            }

            if (attempt < maxRetries) {
                val delay = calculateDelay(attempt)
                Thread.sleep(delay)
            }
        }

        lastResponse?.let { return it }
        throw lastException ?: IOException("Unknown error after $maxRetries retries")
    }

    private fun shouldRetry(code: Int): Boolean = when (code) {
        HTTP_TOO_MANY_REQUESTS -> true
        in HTTP_SERVER_ERROR_RANGE -> true
        else -> false
    }

    private fun calculateDelay(attempt: Int): Long {
        val delay = initialDelayMs * backoffMultiplier
            .pow(attempt.toDouble())
            .toLong()
        return delay.coerceAtMost(maxDelayMs)
    }

    private companion object {
        const val DEFAULT_MAX_RETRIES = 3
        const val DEFAULT_INITIAL_DELAY_MS = 1000L
        const val DEFAULT_MAX_DELAY_MS = 10000L
        const val DEFAULT_BACKOFF_MULTIPLIER = 2.0
        const val HTTP_TOO_MANY_REQUESTS = 429
        val HTTP_SERVER_ERROR_RANGE = 500..599
    }
}
