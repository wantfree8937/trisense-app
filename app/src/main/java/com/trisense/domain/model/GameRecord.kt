package com.trisense.domain.model

data class GameRecord(
    val id: Long = 0,
    val gameType: GameType,
    val score: Long, // Processed as milliseconds
    val createdAt: Long = System.currentTimeMillis()
)
