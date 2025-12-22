package com.trisense.presentation.timing

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
import kotlin.math.abs

enum class TimingGameState {
    IDLE,
    RUNNING,
    RESULT
}

data class TimingState(
    val gameState: TimingGameState = TimingGameState.IDLE,
    val currentTime: Long = 0L, // Current elapsed time in ms
    val targetTime: Long = 5000L,
    val blindThreshold: Long = 3000L,
    val isBlind: Boolean = false,
    val resultTime: Long = 0L,
    val difference: Long = 0L
)

@HiltViewModel
class TimingViewModel @Inject constructor(
    private val saveScoreUseCase: SaveScoreUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TimingState())
    val state = _state.asStateFlow()

    private var startTime: Long = 0
    private var timerJob: Job? = null

    init {
        val initialTargetSeconds = (5..15).random()
        val initialTargetTime = initialTargetSeconds * 1000L
        val initialBlindThreshold = (initialTargetTime * 0.6).toLong()
        
        _state.value = TimingState(
            targetTime = initialTargetTime,
            blindThreshold = initialBlindThreshold
        )
    }

    fun onAction() {
        when (_state.value.gameState) {
            TimingGameState.IDLE, TimingGameState.RESULT -> startGame()
            TimingGameState.RUNNING -> stopGame()
        }
    }

    private fun startGame() {
        timerJob?.cancel()
        startTime = System.currentTimeMillis()
        
        // If coming from RESULT (restart), generate NEW target.
        // If coming from IDLE (first start), use EXISTING target (already shown to user).
        val isRestart = _state.value.gameState == TimingGameState.RESULT
        
        var targetTime = _state.value.targetTime
        var blindThreshold = _state.value.blindThreshold
        
        if (isRestart) {
            val targetSeconds = (5..15).random()
            targetTime = targetSeconds * 1000L
            blindThreshold = (targetTime * 0.6).toLong()
        }

        _state.update { 
            it.copy(
                gameState = TimingGameState.RUNNING, 
                currentTime = 0L, 
                targetTime = targetTime,
                blindThreshold = blindThreshold,
                isBlind = false,
                resultTime = 0L,
                difference = 0L
            ) 
        }

        timerJob = viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                val elapsed = now - startTime
                
                _state.update { 
                    it.copy(
                        currentTime = elapsed,
                        isBlind = elapsed >= it.blindThreshold
                    ) 
                }
                delay(16) // ~60fps update
            }
        }
    }

    private fun stopGame() {
        timerJob?.cancel()
        val endTime = System.currentTimeMillis()
        val finalTime = endTime - startTime
        val targetTime = _state.value.targetTime
        val diff = finalTime - targetTime

        _state.update {
            it.copy(
                gameState = TimingGameState.RESULT,
                resultTime = finalTime,
                difference = diff
            )
        }
        
        saveRecord(abs(diff)) // Save the error margin as the score (lower is better)
    }

    private fun saveRecord(score: Long) {
        viewModelScope.launch {
            saveScoreUseCase(
                GameRecord(
                    gameType = GameType.TIMING,
                    score = score
                )
            )
        }
    }
}
