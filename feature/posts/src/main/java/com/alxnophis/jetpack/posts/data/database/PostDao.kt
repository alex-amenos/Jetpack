package com.alxnophis.jetpack.posts.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alxnophis.jetpack.posts.data.database.entity.PostEntity

@Dao
internal interface PostDao {
    @Query("SELECT * FROM posts")
    suspend fun getAllPosts(): List<PostEntity>

    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: Long): PostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePostById(postId: Int)
}
