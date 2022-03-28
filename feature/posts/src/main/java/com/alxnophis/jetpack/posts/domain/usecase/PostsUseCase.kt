package com.alxnophis.jetpack.posts.domain.usecase

import com.alxnophis.jetpack.posts.data.repository.PostsRepository

class PostsUseCase(
    private val repository: PostsRepository
) {
    suspend operator fun invoke() = repository.getPosts()
}
