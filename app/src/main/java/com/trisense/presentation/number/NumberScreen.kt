package com.trisense.presentation.number

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trisense.presentation.component.PrimaryButton
import java.util.Locale

// Colors
val BgBackground = Color(0xFFF8FAFC)
val Slate900 = Color(0xFF0F172A)
val Slate500 = Color(0xFF64748B)

@Composable
fun NumberScreen(
    onBack: () -> Unit,
    viewModel: NumberViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    // Background color animation for error
    val backgroundColor by animateColorAsState(
        targetValue = if (state.isError) MaterialTheme.colorScheme.error.copy(alpha=0.3f) else BgBackground,
        label = "bgColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (state.gameState) {
            NumberGameState.IDLE -> {
                PrimaryButton(text = "Start", onClick = { viewModel.startGame() })
            }
            NumberGameState.RUNNING -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val totalTime = state.currentTime + state.penaltyTime
                    Text(
                        text = String.format(Locale.getDefault(), "%.2f", totalTime / 1000f),
                        style = MaterialTheme.typography.displayMedium,
                        color = Slate900
                    )
                    Text("Next: ${state.nextTarget}", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.grid) { number ->
                            NumberCell(
                                number = number,
                                isNext = number == state.nextTarget,
                                isCleared = number < state.nextTarget,
                                onClick = { viewModel.onNumberClick(number) }
                            )
                        }
                    }
                }
            }
            NumberGameState.FINISHED -> {
                 Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val finalTime = state.currentTime + state.penaltyTime
                    Text("CLEAR!", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${String.format(Locale.getDefault(), "%.3f", finalTime / 1000f)}s",
                        style = MaterialTheme.typography.displayMedium,
                        color = Slate900
                    )
                     if (state.penaltyTime > 0) {
                         Text("(Penalty: +${state.penaltyTime/1000f}s included)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                     }
                    Spacer(modifier = Modifier.height(32.dp))
                    PrimaryButton(text = "Play Again", onClick = { viewModel.startGame() })
                }
            }
        }
    }
}

@Composable
fun NumberCell(
    number: Int,
    isNext: Boolean,
    isCleared: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isCleared -> Color.Transparent // Dimmed effect or transparent if cleared
        else -> Color.White
    }
    
    val textColor = if (isCleared) Color.Transparent else Slate900
    
    // Border for visuals in light mode? Maybe shadow.
    // Assuming simple white logic as per request.

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .clickable(enabled = !isCleared, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (!isCleared) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
