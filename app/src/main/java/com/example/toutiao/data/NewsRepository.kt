package com.example.toutiao.data

import com.example.toutiao.data.db.NewsDao
import com.example.toutiao.data.model.NewsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val dao: NewsDao
) {
    fun newsFlow(): Flow<List<NewsEntity>> = dao.getAllNews()

    suspend fun refresh() {
        val list = MockDataSource.generate()
        dao.clearAll()
        dao.insertAll(list)
    }

    suspend fun loadMore() {
        val more = MockDataSource.generate(10)
        dao.insertAll(more)
    }
}