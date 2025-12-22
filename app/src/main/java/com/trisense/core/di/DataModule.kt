package com.trisense.core.di

import android.app.Application
import androidx.room.Room
import com.trisense.data.repository.GameRepositoryImpl
import com.trisense.data.source.local.AppDatabase
import com.trisense.data.source.local.GameRecordDao
import com.trisense.domain.repository.GameRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "trisense_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideGameRecordDao(db: AppDatabase): GameRecordDao {
        return db.gameRecordDao()
    }

    @Provides
    @Singleton
    fun provideGameRepository(dao: GameRecordDao): GameRepository {
        return GameRepositoryImpl(dao)
    }
}
