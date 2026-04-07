package com.alxnophis.jetpack.posts.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alxnophis.jetpack.posts.data.datasource.PostDao
import com.alxnophis.jetpack.posts.data.model.PostEntity

@Database(
    entities = [PostEntity::class],
    version = 1,
    exportSchema = false,
)
internal abstract class PostsDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao

    internal companion object {
        const val DATABASE_NAME = "posts_database"
    }
}
