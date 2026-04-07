package com.alxnophis.jetpack.posts.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts_metadata")
internal data class PostsMetadataEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String = POSTS_METADATA_ID,
    @ColumnInfo(name = "last_update_timestamp") val lastUpdateTimestamp: Long,
) {
    internal companion object {
        const val POSTS_METADATA_ID = "posts_metadata"
    }
}
