package com.alxnophis.jetpack.posts.data.model

import com.alxnophis.jetpack.kotlin.constants.EMPTY
import com.alxnophis.jetpack.kotlin.constants.ZERO_INT

internal object PostMother {

    operator fun invoke(
        id: Int = ZERO_INT,
        userId: Int = ZERO_INT,
        title: String = EMPTY,
        body: String = EMPTY
    ) = Post(
        id = id,
        userId = userId,
        title = title,
        body = body
    )
}
