package com.alxnophis.jetpack.api.jsonplaceholder.model

import com.alxnophis.jetpack.kotlin.constants.EMPTY
import com.alxnophis.jetpack.kotlin.constants.ZERO_INT
import org.jetbrains.annotations.TestOnly

@TestOnly
object PostApiModelMother {
    operator fun invoke(
        id: Int = ZERO_INT,
        userId: Int = ZERO_INT,
        title: String = EMPTY,
        body: String = EMPTY,
    ) = PostApiModel(
        id = id,
        userId = userId,
        title = title,
        body = body,
    )
}
