package com.alxnophis.jetpack.testing.extensions

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.*

/**
 * Creates a [TestCoroutineDispatcher] for each test method, sets it
 * as the [Main dispatcher][Dispatchers.setMain] and resets it after the test.
 * The dispatcher can be resolved as a [parameter][ParameterResolver] in the test.
 *
 * Link: https://github.com/ralfstuckert/kotlin-coroutines-test/blob/master/src/test/kotlin/coroutines/MainDispatcherExtension.kt
 * Post: [Testing Coroutines — Dispatchers](https://medium.com/@ralf.stuckert/testing-coroutines-dispatchers-956969cfd374)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherExtension : ParameterResolver, BeforeEachCallback, AfterEachCallback {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun supportsParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ) = parameterContext.parameter.type.isAssignableFrom(TestCoroutineDispatcher::class.java)

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any? =
        extensionContext.testDispatcher

    override fun beforeEach(extensionContext: ExtensionContext) {
        val dispatcher = TestCoroutineDispatcher()
        extensionContext.testDispatcher = dispatcher
        Dispatchers.setMain(dispatcher)
    }

    override fun afterEach(extensionContext: ExtensionContext) {
        Dispatchers.resetMain()
        extensionContext.testDispatcher?.cleanupTestCoroutines()
    }
}

private const val DISPATCHER = "TestCoroutineDispatcher"

@OptIn(ExperimentalCoroutinesApi::class)
private var ExtensionContext.testDispatcher: TestCoroutineDispatcher?
    get() = store[DISPATCHER] as? TestCoroutineDispatcher
    set(value) {
        store.put(DISPATCHER, value)
    }

private val ExtensionContext.store
    get() = this.getStore(
        ExtensionContext.Namespace.create(
            MainDispatcherExtension::class.java,
            this.requiredTestMethod
        )
    )
