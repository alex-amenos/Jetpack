package com.alxnophis.jetpack.api.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

/**
 * OkHttp interceptor that checks network availability before executing requests.
 *
 * This interceptor works in coordination with CacheInterceptor:
 * - If no network: Adds a header flag for CacheInterceptor to attempt stale cache
 * - If network available: Proceeds normally with network call
 *
 * Benefits:
 * - Faster failure feedback to users (no timeout wait)
 * - Better error messaging (distinguish "no internet" from "server error")
 * - Reduced battery consumption (avoids doomed-to-fail connection attempts)
 * - Allows cache fallback when offline
 * - Clearer error handling in the data layer
 *
 * Usage:
 * ```
 * val okHttpClient = OkHttpClient.Builder()
 *     .addInterceptor(NetworkStatusInterceptor(context))
 *     .addInterceptor(CacheInterceptor())
 *     .build()
 * ```
 *
 * @param context Android context for accessing ConnectivityManager
 */
internal class NetworkStatusInterceptor(
    private val context: Context,
) : Interceptor {

    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Check network availability
        val hasNetwork = isNetworkAvailable()

        if (!hasNetwork) {
            Timber.d("NetworkStatusInterceptor: No network available, adding X-No-Network header")
            // Add header to signal CacheInterceptor that we're offline
            // This allows CacheInterceptor to try stale cache before failing
            val offlineRequest = request
                .newBuilder()
                .header(HEADER_NO_NETWORK, "true")
                .build()

            // Proceed with modified request - CacheInterceptor will handle cache fallback
            return chain.proceed(offlineRequest)
        }

        // Network available, proceed normally
        Timber.d("NetworkStatusInterceptor: Network available, proceeding with request")
        return chain.proceed(request)
    }

    /**
     * Checks if network connectivity is available.
     *
     * - Uses NetworkCapabilities to check for internet capability
     * - Supports all transport types (WIFI, CELLULAR, ETHERNET, VPN, etc.)
     * - Does not require network validation to support VPN kill-switch scenarios
     *   where the VPN may block all traffic when disconnected
     *
     * @return true if network has internet capability and a valid transport, false otherwise
     */
    private fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val hasTransport = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)

        return hasInternet && hasTransport
    }

    companion object {
        const val HEADER_NO_NETWORK = "X-No-Network"
    }
}
