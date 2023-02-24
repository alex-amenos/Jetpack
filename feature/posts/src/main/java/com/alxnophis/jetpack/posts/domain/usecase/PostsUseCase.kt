package com.alxnophis.jetpack.posts.domain.usecase

import arrow.core.Either
import com.alxnophis.jetpack.posts.data.repository.PostsRepository
import com.alxnophis.jetpack.posts.domain.model.Post
import com.alxnophis.jetpack.posts.domain.model.PostsError

class PostsUseCase(
    private val repository: PostsRepository
) {
    suspend operator fun invoke(): Either<PostsError, List<Post>> = repository.getPosts()
}
