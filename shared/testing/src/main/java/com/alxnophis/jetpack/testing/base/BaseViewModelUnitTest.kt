package com.alxnophis.jetpack.testing.base

import androidx.annotation.VisibleForTesting
import com.alxnophis.jetpack.testing.extension.InstantExecutorExtension
import org.junit.jupiter.api.extension.ExtendWith

@VisibleForTesting
@ExtendWith(InstantExecutorExtension::class)
abstract class BaseViewModelUnitTest
