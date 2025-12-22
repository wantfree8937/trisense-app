package com.trisense.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trisense.domain.model.GameType
import kotlinx.coroutines.flow.Flow

@Dao
interface GameRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameRecord(record: GameRecordEntity)

    // Using CASE to order ASC for REFLEX (lower is better) and NUMBER (lower is better), 
    // but DESC for TIMING? Wait, TIMING result is "error" (lower is better).
    // Actually, for all these games, LOWER score (time) is usually better.
    // Reflex: faster is better (lower ms)
    // Timing: error margin, lower is better.
    // Number: total time, lower is better.
    // So distinct logic: Always ASC order for "best" score in these specific games.
    @Query("SELECT * FROM game_records WHERE gameType = :gameType ORDER BY score ASC LIMIT :limit")
    fun getTopRecords(gameType: GameType, limit: Int): Flow<List<GameRecordEntity>>
}
