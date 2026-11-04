package com.alxnophis.jetpack.radio.di

import android.content.Context
import com.alxnophis.jetpack.api.radiobrowser.RadioBrowserStationsService
import org.junit.jupiter.api.Test
import org.koin.test.verify.verify

internal class RadioModuleTest {
    @Test
    fun `GIVEN radioModule WHEN verified THEN all definitions resolve`() {
        radioModule.verify(
            extraTypes = listOf(Context::class, RadioBrowserStationsService::class),
        )
    }
}
