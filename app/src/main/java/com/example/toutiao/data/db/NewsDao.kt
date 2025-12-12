package com.example.toutiao.data.db

import androidx.room.*
import com.example.toutiao.data.model.NewsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM news_table ORDER BY rowid ASC")
    fun getAllNews(): Flow<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<NewsEntity>)

    @Query("DELETE FROM news_table")
    suspend fun clearAll()
}
