package com.trisense.domain.use_case

import com.trisense.domain.model.GameRecord
import com.trisense.domain.model.GameType
import com.trisense.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRankingUseCase @Inject constructor(
    private val repository: GameRepository
) {
    operator fun invoke(gameType: GameType): Flow<List<GameRecord>> {
        return repository.getTopRecords(gameType)
    }
}
