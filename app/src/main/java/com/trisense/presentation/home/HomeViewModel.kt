package com.trisense.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trisense.core.util.mutableEventFlow
import com.trisense.domain.model.GameType
import com.trisense.domain.use_case.GetRankingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val reflexBest: String = "--",
    val timingBest: String = "--",
    val numberBest: String = "--"
)

sealed interface HomeEvent {
    data class NavigateToGame(val gameType: GameType) : HomeEvent
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRankingUseCase: GetRankingUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _event = mutableEventFlow<HomeEvent>()
    val event = _event.asSharedFlow()

    init {
        fetchBestScores()
    }

    private fun fetchBestScores() {
        viewModelScope.launch {
            combine(
                getRankingUseCase(GameType.REFLEX),
                getRankingUseCase(GameType.TIMING),
                getRankingUseCase(GameType.NUMBER)
            ) { reflex, timing, number ->
                Triple(reflex, timing, number)
            }.collect { (reflex, timing, number) ->
                _state.update {
                    it.copy(
                        reflexBest = reflex.firstOrNull()?.let { "${it.score} ms" } ?: "--",
                        timingBest = timing.firstOrNull()?.let { "${it.score} ms" } ?: "--",
                        numberBest = number.firstOrNull()?.let { "${it.score / 1000f} s" } ?: "--"
                    )
                }
            }
        }
    }

    fun onGameSelected(gameType: GameType) {
        viewModelScope.launch {
            _event.emit(HomeEvent.NavigateToGame(gameType))
        }
    }
}
