package com.trisense.presentation.reflex

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.hilt.navigation.compose.hiltViewModel
import com.trisense.domain.model.GameType
import com.trisense.domain.model.Grade
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.trisense.presentation.component.GradeLegendDialog
import com.trisense.domain.model.calculateGrade
import androidx.compose.foundation.layout.size

// Colors
val BgBackground = Color(0xFFF8FAFC)
val Slate900 = Color(0xFF0F172A)
val Slate500 = Color(0xFF64748B)

@Composable
fun ReflexScreen(
    onBack: () -> Unit,
    viewModel: ReflexViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val backgroundColor = when (state.gameState) {
        ReflexGameState.IDLE -> BgBackground
        ReflexGameState.READY -> Color(0xFFEF4444) // Red
        ReflexGameState.GO -> Color(0xFF22C55E) // Green
        ReflexGameState.EARLY_START -> Color(0xFFF59E0B) // Amber/Warning
        ReflexGameState.RESULT -> BgBackground
    }

    val contentColor = when(state.gameState) {
        ReflexGameState.READY, ReflexGameState.GO -> Color.White
        else -> Slate900
    }

    var showHelpDialog by remember { mutableStateOf(false) }

    if (showHelpDialog) {
        GradeLegendDialog(
            gameType = GameType.REFLEX,
            onDismissRequest = { showHelpDialog = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable { viewModel.onScreenTouch() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Info Icon
        if (state.gameState == ReflexGameState.IDLE || state.gameState == ReflexGameState.RESULT) {
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
                ReflexGameState.IDLE -> {
                    Text("Tap to Start", style = MaterialTheme.typography.displayMedium, color = contentColor)
                    Text("Wait for Green", style = MaterialTheme.typography.bodyLarge, color = Slate500)
                }
                ReflexGameState.READY -> {
                    Text("Wait...", style = MaterialTheme.typography.displayMedium, color = contentColor)
                }
                ReflexGameState.GO -> {
                    Text("TAP!", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Black, color = contentColor)
                }
                ReflexGameState.EARLY_START -> {
                    Text("Too Early!", style = MaterialTheme.typography.displayMedium, color = contentColor)
                    Text("Tap to Try Again", style = MaterialTheme.typography.bodyLarge, color = contentColor)
                }
                ReflexGameState.RESULT -> {
                    val grade = calculateGrade(GameType.REFLEX, state.resultTime)
                    
                    Text(
                        text = grade.label,
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
                        fontWeight = FontWeight.Black,
                        color = Color(grade.colorHex.toInt())
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("${state.resultTime} ms", style = MaterialTheme.typography.displayMedium, color = Slate900, fontWeight = FontWeight.Bold)
                    Text("Tap to Try Again", style = MaterialTheme.typography.bodyLarge, color = Slate500)
                }
            }
        }
    }
}
