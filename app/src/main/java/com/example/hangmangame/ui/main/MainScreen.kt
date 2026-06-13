package com.example.hangmangame.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.example.hangmangame.components.HangmanCanvas
import com.example.hangmangame.components.Keyboard
import com.example.hangmangame.components.StatsBanner
import com.example.hangmangame.components.WordDisplay
import com.example.hangmangame.theme.HangmanGameTheme
import com.example.hangmangame.theme.NeonCyan
import com.example.hangmangame.theme.NeonGreen
import com.example.hangmangame.theme.NeonIndigo
import com.example.hangmangame.theme.NeonPurple
import com.example.hangmangame.theme.NeonRed
import com.example.hangmangame.theme.TextMuted
import com.example.hangmangame.theme.TextPrimary
import com.example.hangmangame.theme.TextSecondary

@Composable
fun MainScreen(
  onItemClick: (NavKey) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: MainScreenViewModel = viewModel { MainScreenViewModel() },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  
  MainScreenContent(
      state = state,
      categories = viewModel.categories,
      onKeyClick = { viewModel.makeGuess(it) },
      onCategorySelect = { viewModel.selectCategory(it) },
      onResetStats = { viewModel.resetStats() },
      onPlayAgain = { viewModel.startNewGame() },
      modifier = modifier
  )
}

@Composable
internal fun MainScreenContent(
    state: GameState,
    categories: List<String>,
    onKeyClick: (Char) -> Unit,
    onCategorySelect: (String) -> Unit,
    onResetStats: () -> Unit,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showHint by remember { mutableStateOf(false) }

    // Auto-hide hint when a new game starts
    var lastWord by remember { mutableStateOf("") }
    if (state.word != lastWord) {
        showHint = false
        lastWord = state.word
    }

    // Freeze states for when the overlay transitions out
    var completedWord by remember { mutableStateOf("") }
    var wasWon by remember { mutableStateOf(true) }
    var endStreakText by remember { mutableStateOf("") }
    var endStatusColor by remember { mutableStateOf(NeonGreen) }
    var endStatusText by remember { mutableStateOf("") }
    var endDescriptionText by remember { mutableStateOf("") }

    if (state.status != GameStatus.PLAYING) {
        completedWord = state.word
        wasWon = state.status == GameStatus.WON
        endStatusColor = if (wasWon) NeonGreen else NeonRed
        endStatusText = if (wasWon) "VICTORY" else "GAME OVER"
        endStreakText = if (wasWon) "Streak: ${state.streak}" else "Best Streak: ${state.bestStreak}"
        endDescriptionText = if (wasWon) {
            "Brilliant job! You guessed the word correctly."
        } else {
            "Out of guesses! The correct word was:"
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header / App Title
            Text(
                text = "NEON HANGMAN",
                color = NeonCyan,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Statistics Banner
            StatsBanner(
                wins = state.wins,
                losses = state.losses,
                streak = state.streak,
                bestStreak = state.bestStreak,
                categories = categories,
                selectedCategory = state.selectedCategory,
                onCategorySelect = onCategorySelect,
                onResetStats = onResetStats
            )

            // Canvas & Category Display Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Current Category Indicator
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonIndigo.copy(alpha = 0.2f))
                                .border(0.5.dp, NeonIndigo, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Category: ${state.category}",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Hint Toggle Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (showHint) NeonPurple.copy(alpha = 0.3f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    0.5.dp,
                                    if (showHint) NeonPurple else MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { showHint = !showHint }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (showHint) "Hide Hint" else "Reveal Hint",
                                color = if (showHint) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Display Hint if toggled
                    AnimatedVisibility(
                        visible = showHint,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(12.dp)
                        ) {
                             Row(verticalAlignment = Alignment.CenterVertically) {
                                 Box(
                                     modifier = Modifier
                                         .padding(end = 8.dp)
                                         .size(16.dp)
                                         .clip(CircleShape)
                                         .background(NeonPurple),
                                     contentAlignment = Alignment.Center
                                 ) {
                                     Text(
                                         text = "i",
                                         color = Color.White,
                                         fontSize = 11.sp,
                                         fontWeight = FontWeight.Bold
                                     )
                                 }
                                Text(
                                    text = state.hint,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Gallows and Hangman
                    HangmanCanvas(incorrectGuessesCount = state.incorrectGuesses)
                }
            }

            // Word blank placeholders
            WordDisplay(
                word = state.word,
                guessedLetters = state.guessedLetters,
                gameStatus = state.status
            )

            // QWERTY keyboard input
            Keyboard(
                guessedLetters = state.guessedLetters,
                word = state.word,
                gameStatus = state.status,
                onKeyClick = onKeyClick
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Win / Loss modal overlay
        AnimatedVisibility(
            visible = state.status != GameStatus.PLAYING,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                val isWon = wasWon
                val statusText = endStatusText
                val statusColor = endStatusColor
                val streakText = endStreakText
                val descriptionText = endDescriptionText

                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(2.dp, statusColor, RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp
                    )

                    Text(
                        text = descriptionText,
                        color = TextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    // Target word in large text
                    Text(
                        text = completedWord,
                        color = if (isWon) NeonCyan else NeonRed,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )

                    // Streak banner
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusColor.copy(alpha = 0.1f))
                            .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = streakText,
                            color = statusColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onPlayAgain,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = statusColor,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "PLAY AGAIN",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    HangmanGameTheme {
        MainScreenContent(
            state = GameState(
                word = "COMPOSE",
                hint = "Modern declarative UI toolkit",
                category = "Programming",
                guessedLetters = setOf('C', 'O', 'M'),
                incorrectGuesses = 2,
                status = GameStatus.PLAYING
            ),
            categories = listOf("All", "Programming", "Animals"),
            onKeyClick = {},
            onCategorySelect = {},
            onResetStats = {},
            onPlayAgain = {}
        )
    }
}
