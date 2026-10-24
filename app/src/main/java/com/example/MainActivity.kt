package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.WordHuntScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameViewModel

enum class AppScreen {
    HOME,
    GAME,
    WORD_HUNT,
    STATS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BornoSudokuApp()
                }
            }
        }
    }
}

@Composable
fun BornoSudokuApp(
    viewModel: GameViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }

    Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
        when (screen) {
            AppScreen.HOME -> {
                HomeScreen(
                    viewModel = viewModel,
                    onStartPuzzle = { puzzle ->
                        viewModel.startPuzzle(puzzle)
                        currentScreen = AppScreen.GAME
                    },
                    onOpenDaily = {
                        viewModel.startDailyPuzzle()
                        currentScreen = AppScreen.GAME
                    },
                    onOpenWordHunt = {
                        currentScreen = AppScreen.WORD_HUNT
                    },
                    onOpenStats = {
                        viewModel.refreshStats()
                        currentScreen = AppScreen.STATS
                    }
                )
            }
            AppScreen.GAME -> {
                GameScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = AppScreen.HOME }
                )
            }
            AppScreen.WORD_HUNT -> {
                WordHuntScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = AppScreen.HOME }
                )
            }
            AppScreen.STATS -> {
                StatsScreen(
                    viewModel = viewModel,
                    onBack = { currentScreen = AppScreen.HOME }
                )
            }
        }
    }
}
