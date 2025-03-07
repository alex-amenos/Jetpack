package com.alxnophis.jetpack.core.base.provider

import java.util.UUID

class BaseRandomProvider {
    @Suppress("MemberVisibilityCanBePrivate")
    fun randomUUID(): UUID = UUID.randomUUID()

    fun mostSignificantBitsRandomUUID(): Long = randomUUID().mostSignificantBits
}
