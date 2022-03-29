package com.alxnophis.jetpack.posts.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.posts.R
import com.alxnophis.jetpack.posts.domain.model.Post
import com.alxnophis.jetpack.posts.domain.model.PostsError
import com.alxnophis.jetpack.posts.domain.usecase.PostsUseCase
import com.alxnophis.jetpack.posts.ui.contract.PostsSideEffect
import com.alxnophis.jetpack.posts.ui.contract.PostsState
import com.alxnophis.jetpack.posts.ui.contract.PostsViewAction
import java.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class PostsViewModel(
    initialState: PostsState = PostsState(),
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO,
    private val postsUseCase: PostsUseCase,
) : BaseViewModel<PostsViewAction, PostsState, PostsSideEffect>(initialState) {

    init {
        handleAction(PostsViewAction.GetPosts)
    }

    override fun handleAction(action: PostsViewAction) {
        when (action) {
            PostsViewAction.GetPosts -> renderPosts()
            PostsViewAction.Finish -> setSideEffect { PostsSideEffect.Finish }
            is PostsViewAction.DismissError -> dismissError(action.errorId)
        }
    }

    private fun renderPosts() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            getPosts().fold(
                { error ->
                    val errorMessages: List<ErrorMessage> = currentState.errorMessages + ErrorMessage(
                        id = UUID.randomUUID().mostSignificantBits,
                        messageId = when (error) {
                            PostsError.Network -> R.string.core_error_network
                            PostsError.Server -> R.string.core_error_server
                            PostsError.Unknown -> R.string.core_error_unknown
                        }
                    )
                    setState {
                        copy(
                            isLoading = false,
                            errorMessages = errorMessages
                        )
                    }
                },
                { posts ->
                    setState {
                        copy(
                            isLoading = false,
                            posts = posts
                        )
                    }
                }
            )
        }
    }

    private suspend fun getPosts(): Either<PostsError, List<Post>> = withContext(dispatcherIO) {
        postsUseCase.invoke()
    }

    private fun dismissError(errorId: Long) {
        viewModelScope.launch {
            val errorMessages = currentState.errorMessages.filterNot { it.id == errorId }
            setState {
                copy(errorMessages = errorMessages)
            }
        }
    }
}
