package com.alxnophis.jetpack.core.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged

internal class ConnectivityNetworkMonitor(
    private val context: Context,
) : NetworkMonitor {
    private val connectivityManager: ConnectivityManager? = context.getSystemService()

    override val isOnline: Flow<Boolean> =
        callbackFlow {
            val manager = connectivityManager
            if (manager == null) {
                channel.trySend(false)
                channel.close()
                return@callbackFlow
            }

            val callback =
                object : NetworkCallback() {
                    private val networks = mutableSetOf<Network>()

                    override fun onAvailable(network: Network) {
                        networks += network
                        channel.trySend(true)
                    }

                    override fun onLost(network: Network) {
                        networks -= network
                        channel.trySend(networks.isNotEmpty())
                    }

                    override fun onCapabilitiesChanged(
                        network: Network,
                        networkCapabilities: NetworkCapabilities,
                    ) {
                        val hasInternet =
                            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        val isValidated =
                            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                        if (hasInternet && isValidated) {
                            networks += network
                        } else {
                            networks -= network
                        }
                        channel.trySend(networks.isNotEmpty())
                    }
                }

            val request =
                NetworkRequest
                    .Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()

            manager.registerNetworkCallback(request, callback)

            channel.trySend(manager.isCurrentlyConnected())

            awaitClose {
                manager.unregisterNetworkCallback(callback)
            }
        }.conflate()
            .distinctUntilChanged()

    private fun ConnectivityManager.isCurrentlyConnected(): Boolean {
        val activeNet = activeNetwork ?: return false
        val capabilities = getNetworkCapabilities(activeNet) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
