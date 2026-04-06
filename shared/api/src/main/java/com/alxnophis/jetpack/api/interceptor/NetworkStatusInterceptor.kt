package com.alxnophis.jetpack.api.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.alxnophis.jetpack.api.exception.NoConnectivityException
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that checks network availability before executing requests.
 *
 * This interceptor prevents unnecessary network calls when there's no internet connection,
 * failing fast with a [NoConnectivityException] instead of waiting for a timeout.
 *
 * Benefits:
 * - Faster failure feedback to users (no timeout wait)
 * - Better error messaging (distinguish "no internet" from "server error")
 * - Reduced battery consumption (avoids doomed-to-fail connection attempts)
 * - Clearer error handling in the data layer
 *
 * Usage:
 * ```
 * val okHttpClient = OkHttpClient.Builder()
 *     .addInterceptor(NetworkStatusInterceptor(context))
 *     .build()
 * ```
 *
 * @param context Android context for accessing ConnectivityManager
 *
 * @throws NoConnectivityException when no network is available
 */
internal class NetworkStatusInterceptor(
    private val context: Context,
) : Interceptor {

    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isNetworkAvailable()) {
            throw NoConnectivityException()
        }
        return chain.proceed(chain.request())
    }

    /**
     * Checks if network connectivity is available.
     *
     * - Uses NetworkCapabilities to check for internet capability
     * - Validates the network connection is working
     * - Supports all transport types (WIFI, CELLULAR, ETHERNET, VPN, etc.)
     *
     * @return true if network is available and validated, false otherwise
     */
    private fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
