package com.example.hangmangame.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

enum class GameStatus {
    PLAYING, WON, LOST
}

data class WordItem(val word: String, val hint: String)

data class GameState(
    val word: String = "",
    val hint: String = "",
    val category: String = "",
    val guessedLetters: Set<Char> = emptySet(),
    val incorrectGuesses: Int = 0,
    val status: GameStatus = GameStatus.PLAYING,
    val wins: Int = 0,
    val losses: Int = 0,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val selectedCategory: String = "All"
)

class MainScreenViewModel : ViewModel() {
    
    private val categoriesMap = mapOf(
        "Programming" to listOf(
            WordItem("KOTLIN", "Modern language for Android development"),
            WordItem("COROUTINE", "Lightweight threads for async programming"),
            WordItem("COMPOSE", "Modern declarative UI toolkit"),
            WordItem("GRADLE", "Build automation tool for Android"),
            WordItem("ACTIVITY", "Android component representing a screen"),
            WordItem("INTENT", "Messaging object to request action"),
            WordItem("DATABASE", "Structured set of data stored in a computer"),
            WordItem("MANIFEST", "XML file declaring app configuration")
        ),
        "Animals" to listOf(
            WordItem("PLATYPUS", "Egg-laying semi-aquatic mammal from Australia"),
            WordItem("CHAMELEON", "Lizard known for changing its skin color"),
            WordItem("KANGAROO", "Marsupial that hops and carries its young in a pouch"),
            WordItem("PENGUIN", "Flightless bird that lives in cold regions"),
            WordItem("DOLPHIN", "Highly intelligent marine mammal"),
            WordItem("CHEETAH", "Fastest land animal on Earth"),
            WordItem("OCTOPUS", "Eight-armed soft-bodied mollusk")
        ),
        "Countries" to listOf(
            WordItem("ARGENTINA", "South American country famous for Tango"),
            WordItem("SWITZERLAND", "European nation known for Alps and watches"),
            WordItem("MADAGASCAR", "Island country off the coast of East Africa"),
            WordItem("SINGAPORE", "Sovereign island city-state in Southeast Asia"),
            WordItem("JAPAN", "East Asian island nation known for sushi"),
            WordItem("BRAZIL", "Largest country in South America, famous for Carnival"),
            WordItem("CANADA", "North American country with a maple leaf on its flag")
        ),
        "Fruits" to listOf(
            WordItem("PINEAPPLE", "Spiky tropical fruit with sweet yellow flesh"),
            WordItem("POMEGRANATE", "Red fruit filled with juicy edible seeds"),
            WordItem("BLUEBERRY", "Small indigo-colored sweet berry"),
            WordItem("DRAGONFRUIT", "Tropical fruit of a cactus with pink skin"),
            WordItem("AVOCADO", "Green, pear-shaped fruit with a large seed"),
            WordItem("STRAWBERRY", "Red sweet fruit with seeds on its surface"),
            WordItem("WATERMELON", "Large fruit with green rind and sweet red pulp")
        )
    )

    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    init {
        startNewGame()
    }

    val categories: List<String>
        get() = listOf("All") + categoriesMap.keys.toList()

    fun startNewGame() {
        val currentCategory = _uiState.value.selectedCategory
        val wordPool = if (currentCategory == "All") {
            categoriesMap.values.flatten()
        } else {
            categoriesMap[currentCategory] ?: categoriesMap.values.flatten()
        }

        if (wordPool.isEmpty()) return

        // Pick a random word, ensuring it's not the same as the current word if possible
        var selected = wordPool.random()
        if (wordPool.size > 1 && selected.word == _uiState.value.word) {
            while (selected.word == _uiState.value.word) {
                selected = wordPool.random()
            }
        }

        // Find the category of the selected word
        val wordCategory = categoriesMap.entries.firstOrNull { entry ->
            entry.value.any { it.word == selected.word }
        }?.key ?: "General"

        _uiState.update {
            it.copy(
                word = selected.word.uppercase(Locale.ROOT),
                hint = selected.hint,
                category = wordCategory,
                guessedLetters = emptySet(),
                incorrectGuesses = 0,
                status = GameStatus.PLAYING
            )
        }
    }

    fun makeGuess(letter: Char) {
        val upperLetter = letter.uppercaseChar()
        val currentState = _uiState.value

        if (currentState.status != GameStatus.PLAYING) return
        if (currentState.guessedLetters.contains(upperLetter)) return

        val newGuessedLetters = currentState.guessedLetters + upperLetter
        val isCorrect = currentState.word.contains(upperLetter)

        if (isCorrect) {
            val isWon = currentState.word.all { newGuessedLetters.contains(it) || it.isWhitespace() }
            if (isWon) {
                val newStreak = currentState.streak + 1
                _uiState.update {
                    it.copy(
                        guessedLetters = newGuessedLetters,
                        status = GameStatus.WON,
                        wins = it.wins + 1,
                        streak = newStreak,
                        bestStreak = maxOf(newStreak, it.bestStreak)
                    )
                }
            } else {
                _uiState.update {
                    it.copy(guessedLetters = newGuessedLetters)
                }
            }
        } else {
            val newIncorrect = currentState.incorrectGuesses + 1
            val isLost = newIncorrect >= 6
            if (isLost) {
                _uiState.update {
                    it.copy(
                        guessedLetters = newGuessedLetters,
                        incorrectGuesses = newIncorrect,
                        status = GameStatus.LOST,
                        losses = it.losses + 1,
                        streak = 0
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        guessedLetters = newGuessedLetters,
                        incorrectGuesses = newIncorrect
                    )
                }
            }
        }
    }

    fun selectCategory(category: String) {
        _uiState.update {
            it.copy(selectedCategory = category)
        }
        startNewGame()
    }

    fun resetStats() {
        _uiState.update {
            it.copy(
                wins = 0,
                losses = 0,
                streak = 0,
                bestStreak = 0
            )
        }
    }
}
