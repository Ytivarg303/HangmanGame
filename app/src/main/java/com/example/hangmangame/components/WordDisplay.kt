package com.example.hangmangame.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hangmangame.theme.NeonCyan
import com.example.hangmangame.theme.NeonRed
import com.example.hangmangame.ui.main.GameStatus

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordDisplay(
    word: String,
    guessedLetters: Set<Char>,
    gameStatus: GameStatus,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.Center,
        maxItemsInEachRow = 10
    ) {
        word.forEach { char ->
            if (char.isWhitespace()) {
                // Spacer for spaces between words (if any)
                Box(modifier = Modifier.size(width = 24.dp, height = 48.dp))
            } else {
                val isGuessed = guessedLetters.contains(char)
                val isLost = gameStatus == GameStatus.LOST

                val displayChar = if (isGuessed || isLost) char.toString() else ""
                val textColor = when {
                    isGuessed -> NeonCyan
                    isLost && !isGuessed -> NeonRed
                    else -> MaterialTheme.colorScheme.onSurface
                }

                val borderStroke = when {
                    isGuessed -> BorderStroke(2.dp, NeonCyan)
                    isLost && !isGuessed -> BorderStroke(2.dp, NeonRed.copy(alpha = 0.6f))
                    else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                }

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(width = 38.dp, height = 48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .border(borderStroke, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = displayChar,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "LetterReveal"
                    ) { text ->
                        Text(
                            text = text,
                            color = textColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
