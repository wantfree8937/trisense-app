package com.trisense.presentation.number

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

data class NumberState(
    val gameState: NumberGameState = NumberGameState.COUNTDOWN,
    val grid: List<Int> = emptyList(),
    val nextTarget: Int = 1,
    val currentTime: Long = 0L,
    val penaltyTime: Long = 0L,
    val isError: Boolean = false,
    val countdownValue: Int = 3
)

enum class NumberGameState {
    IDLE,
    COUNTDOWN,
    RUNNING,
    FINISHED
}

@HiltViewModel
class NumberViewModel @Inject constructor(
    private val saveScoreUseCase: SaveScoreUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NumberState())
    val state = _state.asStateFlow()

    private var startTime: Long = 0
    private var timerJob: Job? = null
    
    init {
        startCountdown()
    }
    
    fun startCountdown() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _state.update { 
                it.copy(
                    gameState = NumberGameState.COUNTDOWN,
                    countdownValue = 3,
                    grid = emptyList() // Clear grid during countdown? Or show preview? Let's clear for now.
                ) 
            }
            delay(1000)
            _state.update { it.copy(countdownValue = 2) }
            delay(1000)
            _state.update { it.copy(countdownValue = 1) }
            delay(1000)
            startGameReal()
        }
    }
    
    // Internal real start
    private fun startGameReal() {
        val numbers = (1..25).shuffled()
        startTime = System.currentTimeMillis()
        
        _state.update {
            NumberState(
                gameState = NumberGameState.RUNNING,
                grid = numbers,
                nextTarget = 1,
                currentTime = 0L,
                penaltyTime = 0L
            )
        }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                if (_state.value.gameState != NumberGameState.RUNNING) break
                val now = System.currentTimeMillis()
                _state.update { it.copy(currentTime = (now - startTime)) }
                delay(30)
            }
        }
    }
    
    // Public method if needed, but we use startCountdown now
    fun startGame() {
        startCountdown()
    }

    fun onNumberClick(number: Int) {
        val currentState = _state.value
        if (currentState.gameState != NumberGameState.RUNNING) return

        if (number == currentState.nextTarget) {
            // Correct
            if (currentState.nextTarget == 25) {
                // Win
                finishGame()
            } else {
                _state.update { it.copy(nextTarget = it.nextTarget + 1) }
            }
        } else {
            // Wrong
            triggerError()
        }
    }

    private fun triggerError() {
        viewModelScope.launch {
            _state.update { it.copy(isError = true, penaltyTime = it.penaltyTime + 500) }
            delay(100)
            _state.update { it.copy(isError = false) }
        }
    }

    private fun finishGame() {
        timerJob?.cancel()
        val finalTime = _state.value.currentTime
        val totalTime = finalTime + _state.value.penaltyTime
        
        _state.update { 
            it.copy(
                gameState = NumberGameState.FINISHED,
                currentTime = finalTime // Display the run time
                // We show penalty separately or added? Spec: "Total time"
            ) 
        }
        
        saveRecord(totalTime)
    }

    private fun saveRecord(score: Long) {
        viewModelScope.launch {
            saveScoreUseCase(
                GameRecord(
                    gameType = GameType.NUMBER,
                    score = score
                )
            )
        }
    }
}
