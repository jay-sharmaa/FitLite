package com.example.fitlite.paging
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PostDao {
    @Query("SELECT COUNT(*) FROM posts WHERE name = :name")
    suspend fun countByName(name: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)  // Changed from IGNORE to REPLACE
    suspend fun insertAll(posts: List<PostEntity>)

    @Query("SELECT * FROM posts WHERE name = :query")
    fun getPagedPosts(query: String): PagingSource<Int, PostEntity>

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun countPosts(): Int

    @Query("DELETE FROM posts")
    suspend fun clearAll()

    // Optional: Add a more targeted delete method
    @Query("DELETE FROM posts WHERE name = :name")
    suspend fun clearByName(name: String)
}