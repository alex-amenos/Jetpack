package com.alxnophis.jetpack.authentication.ui.view

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
