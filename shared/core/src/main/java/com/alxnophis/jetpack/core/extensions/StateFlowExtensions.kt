package com.alxnophis.jetpack.core.extensions

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

fun <T> T.asStateFlow(): StateFlow<T> = MutableStateFlow(this).asStateFlow()

fun <T> T.asMutableStateFlow(): MutableStateFlow<T> = MutableStateFlow(this)
