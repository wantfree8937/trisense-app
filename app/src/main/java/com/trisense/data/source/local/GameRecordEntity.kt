package com.trisense.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.trisense.domain.model.GameType

@Entity(tableName = "game_records")
data class GameRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gameType: GameType,
    val score: Long,
    val createdAt: Long
)
