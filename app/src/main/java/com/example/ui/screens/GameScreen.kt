package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BengaliWordPuzzles
import com.example.model.GridType
import com.example.model.PuzzleDefinition
import com.example.ui.components.BengaliKeypad
import com.example.ui.components.RulesDialog
import com.example.ui.components.SudokuGrid
import com.example.ui.components.VictoryDialog
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldTertiary
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val currentPuzzle by viewModel.currentPuzzle.collectAsState()
    val cells by viewModel.cells.collectAsState()
    val selectedCell by viewModel.selectedCell.collectAsState()
    val isNoteMode by viewModel.isNoteMode.collectAsState()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val isGameWon by viewModel.isGameWon.collectAsState()
    val mistakesCount by viewModel.mistakesCount.collectAsState()
    val hintsRemaining by viewModel.hintsRemaining.collectAsState()
    val validationMessage by viewModel.validationMessage.collectAsState()
    val highlightSame by viewModel.highlightSame.collectAsState()

    var showRulesDialog by remember { mutableStateOf(false) }
    var showPauseDialog by remember { mutableStateOf(false) }
    var showRestartConfirm by remember { mutableStateOf(false) }

    val puzzle = currentPuzzle ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = puzzle.gridType.titleBn,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "বর্ণ: ${puzzle.letters.joinToString(" ")}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_game_back")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.pauseTimer()
                            showPauseDialog = true
                        },
                        modifier = Modifier.testTag("btn_pause")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pause",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { showRestartConfirm = true },
                        modifier = Modifier.testTag("btn_restart")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restart",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { showRulesDialog = true },
                        modifier = Modifier.testTag("btn_rules")
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Help",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EmeraldPrimary)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Stats bar: Timer & Mistakes Counter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Timer
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = BengaliWordPuzzles.formatBengaliTime(elapsedSeconds),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Mistakes Count
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            if (mistakesCount > 0) Color(0xFFFEE2E2) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "ভুল: ${BengaliWordPuzzles.toBengaliNumber(mistakesCount)}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = if (mistakesCount > 0) Color(0xFFDC2626) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Word Secret Clue Banner (if available)
            if (!puzzle.secretWordClue.isNullOrEmpty()) {
                Surface(
                    color = EmeraldTertiary.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = EmeraldTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = puzzle.secretWordClue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Validation or status message banner
            AnimatedVisibility(
                visible = validationMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                validationMessage?.let { msg ->
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = msg,
                            color = Color(0xFFDC2626),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            // The Interactive Sudoku Grid
            SudokuGrid(
                gridType = puzzle.gridType,
                letters = puzzle.letters,
                cells = cells,
                selectedCell = selectedCell,
                highlightSameLetter = highlightSame,
                onCellClick = { r, c -> viewModel.selectCell(r, c) }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // The Bengali Virtual Keypad & Controls
            BengaliKeypad(
                letters = puzzle.letters,
                cells = cells,
                isNoteMode = isNoteMode,
                hintsRemaining = hintsRemaining,
                onLetterClick = { letterIdx -> viewModel.inputLetter(letterIdx) },
                onEraseClick = { viewModel.eraseCell() },
                onToggleNoteMode = { viewModel.toggleNoteMode() },
                onUndoClick = { viewModel.undo() },
                onHintClick = { viewModel.useHint() },
                onValidateClick = { viewModel.validateManually() }
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    val scoreResult by viewModel.latestScoreResult.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    // Victory Dialog
    if (isGameWon) {
        VictoryDialog(
            puzzle = puzzle,
            elapsedSeconds = elapsedSeconds,
            mistakes = mistakesCount,
            scoreResult = scoreResult,
            onShare = { viewModel.shareVictory(context) },
            onPlayAgain = { viewModel.restartCurrentGame() },
            onNextPuzzle = {
                // Advance to next puzzle in catalogue
                val allPuzzles = BengaliWordPuzzles.PUZZLES_6X6 + BengaliWordPuzzles.PUZZLES_WORD_SECRET + BengaliWordPuzzles.PUZZLES_4X4
                val currentIndex = allPuzzles.indexOfFirst { it.id == puzzle.id }
                val next = if (currentIndex in 0 until allPuzzles.size - 1) {
                    allPuzzles[currentIndex + 1]
                } else {
                    allPuzzles[0]
                }
                viewModel.startPuzzle(next)
            },
            onDismiss = onBack
        )
    }

    // Pause Dialog
    if (showPauseDialog) {
        AlertDialog(
            onDismissRequest = {
                showPauseDialog = false
                viewModel.resumeTimer()
            },
            title = {
                Text(text = "খেলা বিরতি", fontWeight = FontWeight.Bold, color = EmeraldPrimary)
            },
            text = {
                Text(text = "খেলা সাময়িক বিরতিতে রয়েছে। চালিয়ে যেতে 'চালু রাখুন' বাটনে চাপুন।")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPauseDialog = false
                        viewModel.resumeTimer()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("চালু রাখুন")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showPauseDialog = false
                    onBack()
                }) {
                    Text("মূল মেনু")
                }
            }
        )
    }

    // Restart Confirmation Dialog
    if (showRestartConfirm) {
        AlertDialog(
            onDismissRequest = { showRestartConfirm = false },
            title = { Text(text = "পুনরায় শুরু করবেন?", fontWeight = FontWeight.Bold) },
            text = { Text(text = "বর্তমান বোর্ডের সকল অগ্রগতি মুছে নতুন করে শুরু হবে।") },
            confirmButton = {
                Button(
                    onClick = {
                        showRestartConfirm = false
                        viewModel.restartCurrentGame()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("হ্যাঁ, শুরু করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestartConfirm = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Rules Dialog
    if (showRulesDialog) {
        RulesDialog(onDismiss = { showRulesDialog = false })
    }
}
