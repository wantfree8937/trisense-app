package com.trisense.data.repository

import com.trisense.data.source.local.GameRecordDao
import com.trisense.data.source.local.GameRecordEntity
import com.trisense.domain.model.GameRecord
import com.trisense.domain.model.GameType
import com.trisense.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val dao: GameRecordDao
) : GameRepository {

    override suspend fun saveRecord(record: GameRecord) {
        dao.insertGameRecord(record.toEntity())
    }

    override fun getTopRecords(gameType: GameType, limit: Int): Flow<List<GameRecord>> {
        return dao.getTopRecords(gameType, limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private fun GameRecord.toEntity(): GameRecordEntity {
        return GameRecordEntity(
            id = id,
            gameType = gameType,
            score = score,
            createdAt = createdAt
        )
    }

    private fun GameRecordEntity.toDomain(): GameRecord {
        return GameRecord(
            id = id,
            gameType = gameType,
            score = score,
            createdAt = createdAt
        )
    }
}
