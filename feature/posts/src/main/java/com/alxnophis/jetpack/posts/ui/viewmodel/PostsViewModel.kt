package com.alxnophis.jetpack.posts.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.posts.R
import com.alxnophis.jetpack.posts.domain.model.Post
import com.alxnophis.jetpack.posts.domain.model.PostsError
import com.alxnophis.jetpack.posts.domain.usecase.PostsUseCase
import com.alxnophis.jetpack.posts.ui.contract.PostsEvent
import com.alxnophis.jetpack.posts.ui.contract.PostsState
import java.util.UUID
import kotlinx.coroutines.launch

internal class PostsViewModel(
    initialState: PostsState,
    private val postsUseCase: PostsUseCase,
) : BaseViewModel<PostsEvent, PostsState>(initialState) {

    init {
        handleEvent(PostsEvent.GetPosts)
    }

    override fun handleEvent(event: PostsEvent) {
        viewModelScope.launch {
            when (event) {
                PostsEvent.GetPosts -> renderPosts()
                is PostsEvent.DismissError -> dismissError(event.errorId)
            }
        }
    }

    private fun renderPosts() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            getPosts().fold(
                { error ->
                    val errorMessages: List<ErrorMessage> = currentState.errorMessages + ErrorMessage(
                        id = UUID.randomUUID().mostSignificantBits,
                        messageId = when (error) {
                            PostsError.Network -> R.string.posts_error_network
                            PostsError.Server -> R.string.posts_error_server
                            PostsError.Unknown -> R.string.posts_error_unknown
                            PostsError.Unexpected -> R.string.posts_error_unexpected
                        }
                    )
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessages = errorMessages
                        )
                    }
                },
                { posts ->
                    updateState {
                        copy(
                            isLoading = false,
                            posts = posts
                        )
                    }
                }
            )
        }
    }

    private suspend fun getPosts(): Either<PostsError, List<Post>> = postsUseCase.invoke()

    private fun dismissError(errorId: Long) {
        val errorMessages = currentState.errorMessages.filterNot { it.id == errorId }
        updateState {
            copy(errorMessages = errorMessages)
        }
    }
}
