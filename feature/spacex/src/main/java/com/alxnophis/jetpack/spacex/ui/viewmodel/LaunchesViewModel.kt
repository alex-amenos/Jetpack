package com.alxnophis.jetpack.spacex.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.alxnophis.jetpack.core.base.formatter.DateFormatter
import com.alxnophis.jetpack.core.base.viewmodel.BaseViewModel
import com.alxnophis.jetpack.core.ui.model.ErrorMessage
import com.alxnophis.jetpack.kotlin.utils.DispatcherProvider
import com.alxnophis.jetpack.spacex.R
import com.alxnophis.jetpack.spacex.data.model.LaunchesError
import com.alxnophis.jetpack.spacex.data.model.PastLaunchDataModel
import com.alxnophis.jetpack.spacex.data.repository.LaunchesRepository
import com.alxnophis.jetpack.spacex.ui.contract.LaunchesEvent
import com.alxnophis.jetpack.spacex.ui.contract.LaunchesState
import com.alxnophis.jetpack.spacex.ui.model.PastLaunchModel
import com.alxnophis.jetpack.spacex.ui.model.mapper.map
import java.util.UUID
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class LaunchesViewModel(
    initialState: LaunchesState,
    private val dateFormatter: DateFormatter,
    private val dispatcherProvider: DispatcherProvider,
    private val launchesRepository: LaunchesRepository,
) : BaseViewModel<LaunchesEvent, LaunchesState>(initialState) {

    init {
        handleEvent(LaunchesEvent.GetPastLaunches)
    }

    override fun handleEvent(event: LaunchesEvent) {
        when (event) {
            LaunchesEvent.GetPastLaunches -> renderPastLaunches(hasToFetchDataFromNetworkOnly = false)
            LaunchesEvent.RefreshPastLaunches -> renderPastLaunches(hasToFetchDataFromNetworkOnly = true)
            is LaunchesEvent.DismissError -> dismissError(event.errorId)
        }
    }

    private fun renderPastLaunches(hasToFetchDataFromNetworkOnly: Boolean) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            getPastLaunches(hasToFetchDataFromNetworkOnly).fold(
                { error ->
                    val errorMessages: List<ErrorMessage> = currentState.errorMessages + ErrorMessage(
                        id = UUID.randomUUID().mostSignificantBits,
                        messageId = when (error) {
                            is LaunchesError.Http -> R.string.core_error_http
                            LaunchesError.Network -> R.string.core_error_network
                            LaunchesError.Parse -> R.string.core_error_parse
                            else -> R.string.core_error_unknown
                        }
                    )
                    setState {
                        copy(
                            isLoading = false,
                            errorMessages = errorMessages
                        )
                    }
                },
                { pastLaunches ->
                    setState {
                        copy(
                            isLoading = false,
                            pastLaunches = pastLaunches
                        )
                    }
                }
            )
        }
    }

    private suspend fun getPastLaunches(hasToFetchDataFromNetworkOnly: Boolean): Either<LaunchesError, List<PastLaunchModel>> =
        withContext(dispatcherProvider.io()) {
            launchesRepository
                .getPastLaunches(hasToFetchDataFromNetworkOnly)
                .map { launches: List<PastLaunchDataModel> -> launches.map(dateFormatter) }
        }

    private fun dismissError(errorId: Long) {
        val errorMessages = currentState.errorMessages.filterNot { it.id == errorId }
        setState {
            copy(errorMessages = errorMessages)
        }
    }
}
