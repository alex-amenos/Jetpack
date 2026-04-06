package com.alxnophis.jetpack.api.jsonplaceholder.model

import arrow.retrofit.adapter.either.networkhandling.HttpError
import arrow.retrofit.adapter.either.networkhandling.IOError
import arrow.retrofit.adapter.either.networkhandling.UnexpectedCallError
import com.alxnophis.jetpack.api.exception.NoConnectivityException
import com.alxnophis.jetpack.kotlin.constants.EMPTY
import org.jetbrains.annotations.TestOnly
import java.io.IOException

@TestOnly
object CallErrorMother {
    fun ioError(cause: IOException = IOException()) = IOError(cause = cause)

    fun noConnectivityError() = IOError(cause = NoConnectivityException())

    fun httpError(
        code: Int,
        message: String = EMPTY,
        body: String = EMPTY,
    ) = HttpError(
        code = code,
        message = message,
        body = body,
    )

    fun unexpectedCallError(cause: Throwable = Throwable()) = UnexpectedCallError(cause = cause)
}
