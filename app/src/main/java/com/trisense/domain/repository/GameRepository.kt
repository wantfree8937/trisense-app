package com.trisense.domain.repository

import com.trisense.domain.model.GameRecord
import com.trisense.domain.model.GameType
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    suspend fun saveRecord(record: GameRecord)
    fun getTopRecords(gameType: GameType, limit: Int = 5): Flow<List<GameRecord>>
}
