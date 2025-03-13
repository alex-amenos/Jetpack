package com.alxnophis.jetpack.authentication.ui.composable

import androidx.compose.runtime.Composable

@Composable
fun AuthorizedFeature(
    userEmail: String,
    onBack: () -> Unit,
) {
    AuthorizedScreen(
        userEmail = userEmail,
        navigateBack = onBack,
    )
}
