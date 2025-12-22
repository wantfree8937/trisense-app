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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trisense.domain.model.GameType
import com.trisense.domain.model.Grade
import com.trisense.domain.model.calculateGrade
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.trisense.presentation.component.GradeLegendDialog
import java.util.Locale
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

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

    var showHelpDialog by remember { mutableStateOf(false) }

    if (showHelpDialog) {
        GradeLegendDialog(
            gameType = GameType.TIMING,
            onDismissRequest = { showHelpDialog = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgBackground)
            .clickable { viewModel.onAction() },
        contentAlignment = Alignment.Center
    ) {
        // Info Icon
        if (state.gameState == TimingGameState.IDLE || state.gameState == TimingGameState.RESULT) {
            androidx.compose.material3.TextButton(
                onClick = { showHelpDialog = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 24.dp, end = 8.dp)
            ) {
                Text("Rank", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate500)
            }
        }

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
                    val grade = calculateGrade(GameType.TIMING, state.difference)
                    
                    Text(
                        text = grade.label,
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
                        fontWeight = FontWeight.Black,
                        color = Color(grade.colorHex.toInt())
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
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
