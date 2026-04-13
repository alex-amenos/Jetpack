package com.alxnophis.jetpack.posts.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alxnophis.jetpack.posts.data.database.entity.PostsMetadataEntity

@Dao
internal interface PostsMetadataDao {
    @Query("SELECT * FROM posts_metadata WHERE id = :id")
    suspend fun getMetadata(id: String): PostsMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadata(metadata: PostsMetadataEntity)
}
