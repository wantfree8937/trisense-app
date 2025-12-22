package com.trisense.presentation.timing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Locale

// Colors
val BgBackground = Color(0xFFF8FAFC)
val Slate900 = Color(0xFF0F172A)
val Slate500 = Color(0xFF64748B)

@Composable
fun TimingScreen(
    onBack: () -> Unit,
    viewModel: TimingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBackground)
            .clickable { viewModel.onAction() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            when (state.gameState) {
                TimingGameState.IDLE -> {
                    val targetSeconds = state.targetTime / 1000f
                    val formattedTarget = String.format(Locale.getDefault(), "%.2f", targetSeconds)
                    Text("Stop at ${formattedTarget}s", style = MaterialTheme.typography.displayMedium, color = Slate900)
                    Text("Tap to Start", style = MaterialTheme.typography.bodyLarge, color = Slate500)
                }
                TimingGameState.RUNNING -> {
                    val timeText = if (state.isBlind) "??.??" else String.format(Locale.getDefault(), "%.2f", state.currentTime / 1000f)
                    Text(
                        text = timeText,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (state.isBlind) Slate900.copy(alpha = 0.5f) else Slate900
                    )
                    val targetSeconds = state.targetTime / 1000f
                    val formattedTarget = String.format(Locale.getDefault(), "%.2f", targetSeconds)
                    Text("Target: ${formattedTarget}s", style = MaterialTheme.typography.titleMedium, color = Slate500)
                }
                TimingGameState.RESULT -> {
                    val formattedTime = String.format(Locale.getDefault(), "%.3f", state.resultTime / 1000f)
                    val diffSign = if (state.difference > 0) "+" else ""
                    
                    Text(
                        text = "${formattedTime}s",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "${diffSign}${state.difference} ms",
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (kotlin.math.abs(state.difference) < 100) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    Text("Tap to Retry", style = MaterialTheme.typography.bodyLarge, color = Slate500)
                }
            }
        }
    }
}
