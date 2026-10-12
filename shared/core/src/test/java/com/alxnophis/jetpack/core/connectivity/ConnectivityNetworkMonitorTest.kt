package com.alxnophis.jetpack.core.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import app.cash.turbine.test
import com.alxnophis.jetpack.testing.base.BaseUnitTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockConstruction
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
internal class ConnectivityNetworkMonitorTest : BaseUnitTest() {
    private val context: Context = mock()
    private val connectivityManager: ConnectivityManager = mock()

    @Test
    fun `GIVEN null ConnectivityManager WHEN observing isOnline THEN emit false`() {
        runTest {
            whenever(context.getSystemService(ConnectivityManager::class.java)).thenReturn(null)
            whenever(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(null)

            val monitor = ConnectivityNetworkMonitor(context)

            monitor.isOnline.test {
                awaitItem() shouldBeEqualTo false
                awaitComplete()
            }
        }
    }

    @Test
    fun `GIVEN connectivity manager WHEN network capabilities change THEN emit online status only when validated`() {
        runTest {
            whenever(context.getSystemService(ConnectivityManager::class.java)).thenReturn(connectivityManager)
            whenever(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
            whenever(connectivityManager.activeNetwork).thenReturn(null)

            val callbackCaptor = ArgumentCaptor.forClass(NetworkCallback::class.java)

            mockConstruction(NetworkRequest.Builder::class.java) { mock, _ ->
                val requestMock = mock(NetworkRequest::class.java)
                whenever(mock.addCapability(any())).thenReturn(mock)
                whenever(mock.build()).thenReturn(requestMock)
            }.use {
                val monitor = ConnectivityNetworkMonitor(context)

                monitor.isOnline.test {
                    // Initial state
                    awaitItem() shouldBeEqualTo false

                    verify(connectivityManager).registerNetworkCallback(any(), callbackCaptor.capture())
                    val callback = callbackCaptor.value

                    val network: Network = mock()
                    val unvalidatedCapabilities: NetworkCapabilities = mock()
                    whenever(unvalidatedCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
                    whenever(unvalidatedCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)).thenReturn(false)

                    // Network changes capability to unvalidated (captive portal)
                    callback.onCapabilitiesChanged(network, unvalidatedCapabilities)
                    runCurrent()
                    expectNoEvents() // Still false, distinctUntilChanged filters duplicate false

                    // Network changes capability to validated
                    val validatedCapabilities: NetworkCapabilities = mock()
                    whenever(validatedCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
                    whenever(validatedCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)).thenReturn(true)
                    callback.onCapabilitiesChanged(network, validatedCapabilities)
                    runCurrent()
                    awaitItem() shouldBeEqualTo true

                    // Network lost
                    callback.onLost(network)
                    runCurrent()
                    awaitItem() shouldBeEqualTo false

                    cancelAndIgnoreRemainingEvents()
                }

                verify(connectivityManager).unregisterNetworkCallback(any<NetworkCallback>())
            }
        }
    }

    @Test
    fun `GIVEN active network with validated internet WHEN initialized THEN emit true initially`() {
        runTest {
            val activeNetworkMock: Network = mock()
            val capabilitiesMock: NetworkCapabilities = mock()
            whenever(capabilitiesMock.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
            whenever(capabilitiesMock.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)).thenReturn(true)
            whenever(connectivityManager.activeNetwork).thenReturn(activeNetworkMock)
            whenever(connectivityManager.getNetworkCapabilities(activeNetworkMock)).thenReturn(capabilitiesMock)
            whenever(context.getSystemService(ConnectivityManager::class.java)).thenReturn(connectivityManager)
            whenever(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)

            mockConstruction(NetworkRequest.Builder::class.java) { mock, _ ->
                val requestMock = mock(NetworkRequest::class.java)
                whenever(mock.addCapability(any())).thenReturn(mock)
                whenever(mock.build()).thenReturn(requestMock)
            }.use {
                val monitor = ConnectivityNetworkMonitor(context)

                monitor.isOnline.test {
                    awaitItem() shouldBeEqualTo true
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }
    }
}
