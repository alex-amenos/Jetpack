package com.alxnophis.jetpack.spacex.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.alxnophis.jetpack.core.base.constants.EMPTY
import com.alxnophis.jetpack.core.base.formatter.BaseDateFormatter
import com.alxnophis.jetpack.core.base.provider.BaseRandomProvider
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
import java.util.Date
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class LaunchesViewModel(
    initialState: LaunchesState,
    private val dateFormatter: BaseDateFormatter,
    private val randomProvider: BaseRandomProvider,
    private val dispatcherProvider: DispatcherProvider,
    private val launchesRepository: LaunchesRepository,
) : BaseViewModel<LaunchesEvent, LaunchesState>(initialState) {

    init {
        setEvent(LaunchesEvent.GetPastLaunches)
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
            getPastLaunches(hasToFetchDataFromNetworkOnly)
                .map { pastLaunches ->
                    pastLaunches.mapTo { date: Date? ->
                        date
                            ?.let { dateFormatter.formatToReadableDateTime(date) }
                            ?: EMPTY
                    }
                }
                .fold(
                    { error: LaunchesError ->
                        val errorMessages: List<ErrorMessage> = currentState.errorMessages + ErrorMessage(
                            id = randomProvider.mostSignificantBitsRandomUUID(),
                            messageId = when (error) {
                                is LaunchesError.Http -> R.string.spacex_error_http
                                LaunchesError.Network -> R.string.spacex_error_network
                                LaunchesError.Parse -> R.string.spacex_error_parse
                                else -> R.string.spacex_error_unknown
                            }
                        )
                        setState {
                            copy(
                                isLoading = false,
                                errorMessages = errorMessages
                            )
                        }
                    },
                    { pastLaunches: List<PastLaunchModel> ->
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

    private suspend fun getPastLaunches(hasToFetchDataFromNetworkOnly: Boolean): Either<LaunchesError, List<PastLaunchDataModel>> =
        launchesRepository.getPastLaunches(hasToFetchDataFromNetworkOnly)

    private fun dismissError(errorId: Long) {
        val errorMessages = currentState.errorMessages.filterNot { it.id == errorId }
        setState {
            copy(errorMessages = errorMessages)
        }
    }

    private suspend fun List<PastLaunchDataModel>.mapTo(formatDate: (Date?) -> String): List<PastLaunchModel> =
        withContext(dispatcherProvider.default()) {
            map { launch ->
                PastLaunchModel(
                    id = launch.id,
                    missionName = launch.missionName ?: EMPTY,
                    details = launch.details ?: EMPTY,
                    rocket = launch.rocketName ?: EMPTY,
                    launchSite = launch.launchSiteShort ?: EMPTY,
                    missionPatchUrl = launch.missionPatchSmallUrl,
                    launchDateUtc = formatDate(launch.launchDateUtc),
                )
            }
        }
}
