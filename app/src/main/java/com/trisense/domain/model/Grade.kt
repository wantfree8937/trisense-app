package com.trisense.domain.model

import kotlin.math.abs

enum class Grade(val label: String, val colorHex: Long) {
    SS("SS", 0xFFE11D48),
    S("S", 0xFF7C3AED),
    A("A", 0xFF2563EB),
    B("B", 0xFF059669),
    C("C", 0xFFD97706),
    D("D", 0xFF525252);
}

fun calculateGrade(gameType: GameType, score: Long): Grade {
    return when (gameType) {
        GameType.REFLEX -> when {
            score <= 210 -> Grade.SS
            score <= 270 -> Grade.S
            score <= 330 -> Grade.A
            score <= 390 -> Grade.B
            score <= 450 -> Grade.C
            else -> Grade.D
        }
        GameType.TIMING -> {
            val error = abs(score)
            when {
                error <= 30 -> Grade.SS
                error <= 60 -> Grade.S
                error <= 120 -> Grade.A
                error <= 240 -> Grade.B
                error <= 480 -> Grade.C
                else -> Grade.D
            }
        }
        GameType.NUMBER -> when {
            score <= 12000 -> Grade.SS
            score <= 15000 -> Grade.S
            score <= 20000 -> Grade.A
            score <= 25000 -> Grade.B
            score <= 35000 -> Grade.C
            else -> Grade.D
        }
    }
}
