package com.alxnophis.jetpack.posts.data.model

import com.alxnophis.jetpack.kotlin.constants.EMPTY
import com.alxnophis.jetpack.kotlin.constants.ZERO_LONG

internal object PostMother {
    operator fun invoke(
        id: Long = ZERO_LONG,
        userId: Long = ZERO_LONG,
        title: String = EMPTY,
        body: String = EMPTY,
    ) = Post(
        id = id,
        userId = userId,
        title = title,
        body = body,
    )
}
