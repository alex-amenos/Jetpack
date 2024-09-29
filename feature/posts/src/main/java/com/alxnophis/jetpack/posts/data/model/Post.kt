package com.alxnophis.jetpack.posts.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
) : Parcelable {
    val titleCapitalized: String
        get() = title.replaceFirstChar { it.uppercase() }
}
