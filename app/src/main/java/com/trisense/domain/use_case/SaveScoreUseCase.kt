package com.trisense.domain.use_case

import com.trisense.domain.model.GameRecord
import com.trisense.domain.repository.GameRepository
import javax.inject.Inject

class SaveScoreUseCase @Inject constructor(
    private val repository: GameRepository
) {
    suspend operator fun invoke(record: GameRecord) {
        repository.saveRecord(record)
    }
}
