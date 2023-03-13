package com.alxnophis.jetpack.core.ui.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.alxnophis.jetpack.kotlin.constants.EMPTY

data class ErrorMessage(
    val id: Long,
    @StringRes val messageId: Int? = null,
    val message: String? = null
) {
    @Composable
    fun composableMessage(): String =
        when {
            messageId != null -> stringResource(id = messageId)
            message != null -> message
            else -> EMPTY
        }
}
