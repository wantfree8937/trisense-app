package com.trisense.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Leaderboard
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trisense.domain.model.GameType
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    onNavigateToGame: (GameType) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is HomeEvent.NavigateToGame -> onNavigateToGame(event.gameType)
            }
        }
    }
    
    val state by viewModel.state.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BgBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- Header ---
            Spacer(modifier = Modifier.height(64.dp))
            Text(
                text = "TriSense",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Slate900,
                    letterSpacing = (-1).sp
                )
            )
            Text(
                text = "COGNITIVE TRAINING",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Slate500,
                    letterSpacing = 2.sp
                ),
                modifier = Modifier.padding(top = 16.dp, bottom = 64.dp)
            )

            // --- Game Cards ---
            Column(
                verticalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                GameCard(
                    title = "Flash Reflex",
                    score = "Best: ${state.reflexBest}",
                    icon = Icons.Rounded.Bolt,
                    themeColor = Indigo600,
                    bgColor = Indigo50,
                    borderColor = Indigo100,
                    glowBrush = Brush.radialGradient(
                        colors = listOf(Indigo600.copy(alpha = 0.1f), Color.Transparent)
                    ),
                    onClick = { viewModel.onGameSelected(GameType.REFLEX) }
                )

                GameCard(
                    title = "Blind Timing",
                    score = "Best: ${state.timingBest}",
                    icon = Icons.Rounded.Timer,
                    themeColor = Teal600,
                    bgColor = Teal50,
                    borderColor = Teal100,
                    glowBrush = Brush.radialGradient(
                        colors = listOf(Teal600.copy(alpha = 0.1f), Color.Transparent)
                    ),
                    onClick = { viewModel.onGameSelected(GameType.TIMING) }
                )

                GameCard(
                    title = "Rapid Grid",
                    score = "Best: ${state.numberBest}",
                    icon = Icons.Rounded.GridView,
                    themeColor = Rose500,
                    bgColor = Rose50,
                    borderColor = Rose100,
                    glowBrush = Brush.radialGradient(
                        colors = listOf(Rose500.copy(alpha = 0.15f), Color.Transparent)
                    ),
                    onClick = { viewModel.onGameSelected(GameType.NUMBER) }
                )
            }

            // Footer removed as requested
        }
    }
}

@Composable
fun GameCard(
    title: String,
    score: String,
    icon: ImageVector,
    themeColor: Color,
    bgColor: Color,
    borderColor: Color,
    glowBrush: Brush,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp) 
    ) {
        Box(modifier = Modifier.fillMaxSize().clickable { onClick() }) {
            // Glow effects
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 20.dp, y = (-20).dp)
                    .background(glowBrush)
            )
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-20).dp, y = 20.dp)
                    .background(glowBrush)
            )

            // Content
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = themeColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Slate800,
                            letterSpacing = (-0.5).sp
                        )
                    )
                }

                // Score Badge
                Surface(
                    color = bgColor,
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                ) {
                    Text(
                        text = score,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColor
                        )
                    )
                }
            }
        }
    }
}

// Colors
val BgBackground = Color(0xFFF8FAFC)
val Slate900 = Color(0xFF0F172A)
val Slate800 = Color(0xFF1E293B)
val Slate500 = Color(0xFF64748B)
val Indigo50 = Color(0xFFEEF2FF)
val Indigo100 = Color(0xFFE0E7FF)
val Indigo600 = Color(0xFF4F46E5)
val Teal50 = Color(0xFFF0FDFA)
val Teal100 = Color(0xFFCCFBF1)
val Teal600 = Color(0xFF0D9488)
val Rose50 = Color(0xFFFFF1F2)
val Rose100 = Color(0xFFFFE4E6)
val Rose500 = Color(0xFFF43F5E)
