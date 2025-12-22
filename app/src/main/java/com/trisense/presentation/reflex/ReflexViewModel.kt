package com.trisense.presentation.reflex

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trisense.domain.model.GameRecord
import com.trisense.domain.model.GameType
import com.trisense.domain.use_case.SaveScoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

enum class ReflexGameState {
    IDLE,       // Initial state or Reset
    READY,      // Red screen, waiting for Green
    GO,         // Green screen, timer running
    EARLY_START,// Touched too early
    RESULT      // Show time
}

data class ReflexState(
    val gameState: ReflexGameState = ReflexGameState.IDLE,
    val resultTime: Long = 0
)

@HiltViewModel
class ReflexViewModel @Inject constructor(
    private val saveScoreUseCase: SaveScoreUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ReflexState())
    val state = _state.asStateFlow()

    private var startTime: Long = 0
    private var gameJob: Job? = null

    fun startGame() {
        gameJob?.cancel()
        gameJob = viewModelScope.launch {
            _state.update { it.copy(gameState = ReflexGameState.READY, resultTime = 0) }
            val randomDelay = Random.nextLong(2000, 5000)
            delay(randomDelay)
            // Go!
            startTime = System.currentTimeMillis()
            _state.update { it.copy(gameState = ReflexGameState.GO) }
        }
    }

    fun onScreenTouch() {
        when (_state.value.gameState) {
            ReflexGameState.READY -> {
                // Early start
                gameJob?.cancel()
                _state.update { it.copy(gameState = ReflexGameState.EARLY_START) }
            }
            ReflexGameState.GO -> {
                // Success
                val endTime = System.currentTimeMillis()
                val duration = endTime - startTime
                _state.update { it.copy(gameState = ReflexGameState.RESULT, resultTime = duration) }
                saveRecord(duration)
            }
            ReflexGameState.EARLY_START, ReflexGameState.RESULT -> {
                // Restart on touch
                startGame()
            }
            ReflexGameState.IDLE -> {
                startGame()
            }
        }
    }

    private fun saveRecord(score: Long) {
        viewModelScope.launch {
            saveScoreUseCase(
                GameRecord(
                    gameType = GameType.REFLEX,
                    score = score
                )
            )
        }
    }
}
