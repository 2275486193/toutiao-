package com.example.toutiao.di

import android.app.Application
import androidx.room.Room
import com.example.toutiao.data.db.AppDatabase
import com.example.toutiao.data.db.NewsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, "toutiao.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideNewsDao(db: AppDatabase): NewsDao = db.newsDao()
}