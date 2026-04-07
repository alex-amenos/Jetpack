package com.alxnophis.jetpack.posts.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alxnophis.jetpack.posts.data.database.entity.PostEntity
import com.alxnophis.jetpack.posts.data.database.entity.PostsMetadataEntity

@Database(
    entities = [PostEntity::class, PostsMetadataEntity::class],
    version = 2,
    exportSchema = false,
)
internal abstract class PostsDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao

    abstract fun postsMetadataDao(): PostsMetadataDao

    internal companion object {
        const val DATABASE_NAME = "posts_database"
    }
}
