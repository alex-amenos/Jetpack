package com.alxnophis.jetpack.authentication.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import com.alxnophis.jetpack.authentication.ui.view.AuthorizedScreen
import com.alxnophis.jetpack.core.base.activity.BaseActivity

class AuthorizedActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        renderContent()
    }

    private fun renderContent() {
        setContent {
            AuthorizedScreen()
        }
    }
}
