package com.alxnophis.jetpack.api.jsonplaceholder.model

import arrow.retrofit.adapter.either.networkhandling.HttpError
import arrow.retrofit.adapter.either.networkhandling.IOError
import arrow.retrofit.adapter.either.networkhandling.UnexpectedCallError
import com.alxnophis.jetpack.kotlin.constants.EMPTY
import java.io.IOException

object CallErrorMother {

    fun ioError(
        cause: IOException = IOException()
    ) = IOError(
        cause = cause
    )

    fun httpError(
        code: Int,
        message: String = EMPTY,
        body: String = EMPTY
    ) = HttpError(
        code = code,
        message = message,
        body = body
    )

    fun unexpectedCallError(
        cause: Throwable = Throwable()
    ) = UnexpectedCallError(
        cause = cause
    )
}
