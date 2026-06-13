package com.example.hangmangame.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hangmangame.theme.NeonGreen
import com.example.hangmangame.theme.TextMuted
import com.example.hangmangame.theme.TextPrimary
import com.example.hangmangame.ui.main.GameStatus

@Composable
fun Keyboard(
    guessedLetters: Set<Char>,
    word: String,
    gameStatus: GameStatus,
    onKeyClick: (Char) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = listOf(
        listOf('Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P'),
        listOf('A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L'),
        listOf('Z', 'X', 'C', 'V', 'B', 'N', 'M')
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        rows.forEachIndexed { rowIndex, row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Proportional spacing for standard QWERTY indentation
                if (rowIndex == 1) {
                    Spacer(modifier = Modifier.weight(0.5f))
                } else if (rowIndex == 2) {
                    Spacer(modifier = Modifier.weight(1.5f))
                }

                row.forEach { letter ->
                    val isGuessed = guessedLetters.contains(letter)
                    val isInWord = word.contains(letter)
                    val isPlaying = gameStatus == GameStatus.PLAYING

                    val keyBackground = when {
                        isGuessed && isInWord -> NeonGreen.copy(alpha = 0.15f)
                        isGuessed && !isInWord -> MaterialTheme.colorScheme.background
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    }

                    val keyBorder = when {
                        isGuessed && isInWord -> BorderStroke(1.5.dp, NeonGreen)
                        isGuessed && !isInWord -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    }

                    val keyTextColor = when {
                        isGuessed && isInWord -> NeonGreen
                        isGuessed && !isInWord -> TextMuted
                        else -> TextPrimary
                    }

                    val isClickable = isPlaying && !isGuessed

                    Box(
                        modifier = Modifier
                            .height(46.dp)
                            .weight(1f) // Distribute key width equally
                            .clip(RoundedCornerShape(6.dp))
                            .background(keyBackground)
                            .border(keyBorder, RoundedCornerShape(6.dp))
                            .clickable(enabled = isClickable) {
                                onKeyClick(letter)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter.toString(),
                            color = keyTextColor,
                            fontSize = 18.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                        )
                    }
                }

                if (rowIndex == 1) {
                    Spacer(modifier = Modifier.weight(0.5f))
                } else if (rowIndex == 2) {
                    Spacer(modifier = Modifier.weight(1.5f))
                }
            }
        }
    }
}
