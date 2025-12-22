package com.trisense.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.trisense.domain.model.GameType

@Database(entities = [GameRecordEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameRecordDao(): GameRecordDao
}

class Converters {
    @TypeConverter
    fun fromGameType(value: GameType): String = value.name

    @TypeConverter
    fun toGameType(value: String): GameType = GameType.valueOf(value)
}
