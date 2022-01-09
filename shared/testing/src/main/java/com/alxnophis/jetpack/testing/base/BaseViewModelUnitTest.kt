package com.alxnophis.jetpack.testing.base

import androidx.annotation.VisibleForTesting
import com.alxnophis.jetpack.testing.extensions.InstantExecutorExtension
import com.alxnophis.jetpack.testing.extensions.MainDispatcherExtension
import org.junit.jupiter.api.extension.ExtendWith

@VisibleForTesting
@ExtendWith(InstantExecutorExtension::class, MainDispatcherExtension::class)
abstract class BaseViewModelUnitTest
