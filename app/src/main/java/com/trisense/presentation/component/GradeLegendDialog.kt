package com.trisense.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trisense.domain.model.GameType
import com.trisense.domain.model.Grade

// Colors
val Slate900 = Color(0xFF0F172A)

@Composable
fun GradeLegendDialog(
    gameType: GameType,
    onDismissRequest: () -> Unit
) {
    val criteria = when (gameType) {
        GameType.REFLEX -> listOf(
            Grade.SS to "0 ~ 210 ms",
            Grade.S to "211 ~ 270 ms",
            Grade.A to "271 ~ 330 ms",
            Grade.B to "331 ~ 390 ms",
            Grade.C to "391 ~ 450 ms",
            Grade.D to "451+ ms"
        )
        GameType.TIMING -> listOf(
            Grade.SS to "±0 ~ 30 ms",
            Grade.S to "±31 ~ 60 ms",
            Grade.A to "±61 ~ 120 ms",
            Grade.B to "±121 ~ 240 ms",
            Grade.C to "±241 ~ 480 ms",
            Grade.D to "±481+ ms"
        )
        GameType.NUMBER -> listOf(
            Grade.SS to "0 ~ 12.00 s",
            Grade.S to "12.01 ~ 15.00 s",
            Grade.A to "15.01 ~ 20.00 s",
            Grade.B to "20.01 ~ 25.00 s",
            Grade.C to "25.01 ~ 35.00 s",
            Grade.D to "35.01+ s"
        )
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Rank System",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Slate900
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                criteria.forEach { (grade, range) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = grade.label,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                                fontWeight = FontWeight.Black,
                                color = Color(grade.colorHex.toInt()),
                                modifier = Modifier.width(48.dp)
                            )
                        }
                        Text(
                            text = range,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                    }
                    if (grade != Grade.D) {
                        Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}
