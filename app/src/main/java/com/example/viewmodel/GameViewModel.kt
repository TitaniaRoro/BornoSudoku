package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BengaliWordPuzzles
import com.example.data.GamePreferences
import com.example.model.GameBadge
import com.example.model.GameStatsData
import com.example.model.GridType
import com.example.model.MoveRecord
import com.example.model.PlayerRank
import com.example.model.PuzzleDefinition
import com.example.model.SudokuCell
import com.example.model.VictoryScoreResult
import com.example.model.WordHuntLevel
import com.example.model.WordHuntWord
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = GamePreferences(application)

    // Current Game State
    private val _currentPuzzle = MutableStateFlow<PuzzleDefinition?>(null)
    val currentPuzzle: StateFlow<PuzzleDefinition?> = _currentPuzzle.asStateFlow()

    private val _cells = MutableStateFlow<List<List<SudokuCell>>>(emptyList())
    val cells: StateFlow<List<List<SudokuCell>>> = _cells.asStateFlow()

    private val _selectedCell = MutableStateFlow<Pair<Int, Int>?>(null)
    val selectedCell: StateFlow<Pair<Int, Int>?> = _selectedCell.asStateFlow()

    private val _isNoteMode = MutableStateFlow(false)
    val isNoteMode: StateFlow<Boolean> = _isNoteMode.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _isGameWon = MutableStateFlow(false)
    val isGameWon: StateFlow<Boolean> = _isGameWon.asStateFlow()

    private val _mistakesCount = MutableStateFlow(0)
    val mistakesCount: StateFlow<Int> = _mistakesCount.asStateFlow()

    private val _hintsRemaining = MutableStateFlow(3)
    val hintsRemaining: StateFlow<Int> = _hintsRemaining.asStateFlow()

    private val _validationMessage = MutableStateFlow<String?>(null)
    val validationMessage: StateFlow<String?> = _validationMessage.asStateFlow()

    private val _stats = MutableStateFlow(preferences.getStats())
    val stats: StateFlow<GameStatsData> = _stats.asStateFlow()

    private val _badges = MutableStateFlow(preferences.getBadgesWithUnlockStatus())
    val badges: StateFlow<List<GameBadge>> = _badges.asStateFlow()

    private val _latestScoreResult = MutableStateFlow<VictoryScoreResult?>(null)
    val latestScoreResult: StateFlow<VictoryScoreResult?> = _latestScoreResult.asStateFlow()

    private val _autoCheck = MutableStateFlow(preferences.isAutoCheckEnabled())
    val autoCheck: StateFlow<Boolean> = _autoCheck.asStateFlow()

    private val _highlightSame = MutableStateFlow(preferences.isHighlightSameNumbersEnabled())
    val highlightSame: StateFlow<Boolean> = _highlightSame.asStateFlow()

    private val moveHistory = ArrayDeque<MoveRecord>()
    private var timerJob: Job? = null

    // Word Hunt Mode State
    private val _currentWordHuntLevel = MutableStateFlow(BengaliWordPuzzles.WORD_HUNT_LEVELS[0])
    val currentWordHuntLevel: StateFlow<WordHuntLevel> = _currentWordHuntLevel.asStateFlow()

    private val _currentComposedWord = MutableStateFlow("")
    val currentComposedWord: StateFlow<String> = _currentComposedWord.asStateFlow()

    private val _wordHuntMessage = MutableStateFlow<String?>(null)
    val wordHuntMessage: StateFlow<String?> = _wordHuntMessage.asStateFlow()

    private val _wordHuntWords = MutableStateFlow<List<WordHuntWord>>(BengaliWordPuzzles.WORD_HUNT_LEVELS[0].targetWords)
    val wordHuntWords: StateFlow<List<WordHuntWord>> = _wordHuntWords.asStateFlow()

    init {
        // Start default puzzle on launch (Classic 6x6)
        startPuzzle(BengaliWordPuzzles.PUZZLES_6X6[0])
    }

    fun refreshStats() {
        _stats.value = preferences.getStats()
        _badges.value = preferences.getBadgesWithUnlockStatus()
    }

    fun setAutoCheck(enabled: Boolean) {
        _autoCheck.value = enabled
        preferences.setAutoCheckEnabled(enabled)
        recomputeConflicts()
    }

    fun setHighlightSame(enabled: Boolean) {
        _highlightSame.value = enabled
        preferences.setHighlightSameNumbersEnabled(enabled)
    }

    fun startPuzzle(puzzle: PuzzleDefinition) {
        timerJob?.cancel()
        _currentPuzzle.value = puzzle
        _elapsedSeconds.value = 0
        _isGameWon.value = false
        _mistakesCount.value = 0
        _hintsRemaining.value = 3
        _validationMessage.value = null
        _latestScoreResult.value = null
        moveHistory.clear()

        val size = puzzle.gridType.size
        val newBoard = List(size) { r ->
            List(size) { c ->
                val initialVal = puzzle.initialBoard[r][c]
                SudokuCell(
                    row = r,
                    col = c,
                    value = initialVal,
                    isGiven = initialVal != 0
                )
            }
        }
        _cells.value = newBoard

        // Find first empty cell to select
        var firstEmpty: Pair<Int, Int>? = null
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (newBoard[r][c].value == 0) {
                    firstEmpty = Pair(r, c)
                    break
                }
            }
            if (firstEmpty != null) break
        }
        _selectedCell.value = firstEmpty ?: Pair(0, 0)

        recomputeConflicts()
        startTimer()
    }

    fun startDailyPuzzle() {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val dayNumber = SimpleDateFormat("d", Locale.getDefault()).format(Date()).toIntOrNull() ?: 1
        val dailyList = listOf(
            BengaliWordPuzzles.PUZZLES_WORD_SECRET[0],
            BengaliWordPuzzles.PUZZLES_6X6[1],
            BengaliWordPuzzles.PUZZLES_WORD_SECRET[1],
            BengaliWordPuzzles.PUZZLES_4X4[0],
            BengaliWordPuzzles.PUZZLES_WORD_SECRET[2]
        )
        val selected = dailyList[dayNumber % dailyList.size]
        startPuzzle(selected)
    }

    private fun startTimer() {
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value && !_isGameWon.value) {
                delay(1000L)
                _elapsedSeconds.value += 1
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
    }

    fun resumeTimer() {
        if (!_isGameWon.value) {
            startTimer()
        }
    }

    fun selectCell(row: Int, col: Int) {
        _selectedCell.value = Pair(row, col)
        _validationMessage.value = null
    }

    fun toggleNoteMode() {
        _isNoteMode.value = !_isNoteMode.value
    }

    fun inputLetter(value: Int) {
        val selected = _selectedCell.value ?: return
        val puzzle = _currentPuzzle.value ?: return
        val (r, c) = selected
        val currentCell = _cells.value.getOrNull(r)?.getOrNull(c) ?: return

        if (currentCell.isGiven || _isGameWon.value) return

        if (_isNoteMode.value) {
            // Toggle note
            val newNotes = if (currentCell.notes.contains(value)) {
                currentCell.notes - value
            } else {
                currentCell.notes + value
            }
            val record = MoveRecord(r, c, currentCell.value, currentCell.value, currentCell.notes, newNotes)
            moveHistory.addLast(record)
            updateCell(r, c) { it.copy(notes = newNotes) }
        } else {
            // Placing letter value
            if (currentCell.value == value) {
                // Tapping same letter again erases it
                eraseCell()
                return
            }

            val record = MoveRecord(r, c, currentCell.value, value, currentCell.notes, emptySet())
            moveHistory.addLast(record)

            updateCell(r, c) { it.copy(value = value, notes = emptySet(), isHinted = false) }

            // Auto-check conflicts
            recomputeConflicts()

            // Check if mistake against solution (if in auto-check mode)
            if (value != puzzle.solutionBoard[r][c]) {
                _mistakesCount.value += 1
            }

            // Check victory condition
            checkVictory()
        }
    }

    fun eraseCell() {
        val selected = _selectedCell.value ?: return
        val (r, c) = selected
        val currentCell = _cells.value.getOrNull(r)?.getOrNull(c) ?: return
        if (currentCell.isGiven || _isGameWon.value) return

        if (currentCell.value != 0 || currentCell.notes.isNotEmpty()) {
            val record = MoveRecord(r, c, currentCell.value, 0, currentCell.notes, emptySet())
            moveHistory.addLast(record)
            updateCell(r, c) { it.copy(value = 0, notes = emptySet(), isHinted = false) }
            recomputeConflicts()
        }
    }

    fun undo() {
        if (moveHistory.isEmpty() || _isGameWon.value) return
        val lastMove = moveHistory.removeLast()
        updateCell(lastMove.row, lastMove.col) {
            it.copy(
                value = lastMove.prevValue,
                notes = lastMove.prevNotes,
                isHinted = false
            )
        }
        _selectedCell.value = Pair(lastMove.row, lastMove.col)
        recomputeConflicts()
    }

    fun useHint() {
        if (_hintsRemaining.value <= 0 || _isGameWon.value) return
        val puzzle = _currentPuzzle.value ?: return
        val size = puzzle.gridType.size

        // Prefer current selected cell if it is empty or wrong
        var target: Pair<Int, Int>? = null
        val currentSel = _selectedCell.value
        if (currentSel != null) {
            val cell = _cells.value[currentSel.first][currentSel.second]
            if (!cell.isGiven && cell.value != puzzle.solutionBoard[currentSel.first][currentSel.second]) {
                target = currentSel
            }
        }

        // If not, find the first unfilled cell
        if (target == null) {
            for (r in 0 until size) {
                for (c in 0 until size) {
                    val cell = _cells.value[r][c]
                    if (!cell.isGiven && cell.value == 0) {
                        target = Pair(r, c)
                        break
                    }
                }
                if (target != null) break
            }
        }

        if (target != null) {
            val (r, c) = target
            val correctVal = puzzle.solutionBoard[r][c]
            val prevCell = _cells.value[r][c]
            moveHistory.addLast(MoveRecord(r, c, prevCell.value, correctVal, prevCell.notes, emptySet()))

            updateCell(r, c) {
                it.copy(
                    value = correctVal,
                    notes = emptySet(),
                    isHinted = true
                )
            }
            _selectedCell.value = target
            _hintsRemaining.value -= 1
            recomputeConflicts()
            checkVictory()
        }
    }

    fun restartCurrentGame() {
        val p = _currentPuzzle.value ?: return
        startPuzzle(p)
    }

    private fun updateCell(r: Int, c: Int, transform: (SudokuCell) -> SudokuCell) {
        val current = _cells.value
        val updated = current.mapIndexed { rowIdx, rowList ->
            if (rowIdx == r) {
                rowList.mapIndexed { colIdx, cell ->
                    if (colIdx == c) transform(cell) else cell
                }
            } else {
                rowList
            }
        }
        _cells.value = updated
    }

    private fun recomputeConflicts() {
        val puzzle = _currentPuzzle.value ?: return
        val size = puzzle.gridType.size
        val current = _cells.value
        if (current.isEmpty()) return

        val intBoard = current.map { row -> row.map { it.value } }
        val conflicts = if (_autoCheck.value) {
            BengaliWordPuzzles.checkCellConflicts(
                board = intBoard,
                size = size,
                blockRows = puzzle.gridType.blockRows,
                blockCols = puzzle.gridType.blockCols
            )
        } else {
            emptySet()
        }

        val updated = current.mapIndexed { r, row ->
            row.mapIndexed { c, cell ->
                cell.copy(isConflict = conflicts.contains(Pair(r, c)))
            }
        }
        _cells.value = updated
    }

    private fun checkVictory() {
        val puzzle = _currentPuzzle.value ?: return
        val size = puzzle.gridType.size
        val current = _cells.value
        val intBoard = current.map { row -> row.map { it.value } }

        if (!BengaliWordPuzzles.isBoardComplete(intBoard, size)) return

        val conflicts = BengaliWordPuzzles.checkCellConflicts(
            intBoard,
            size,
            puzzle.gridType.blockRows,
            puzzle.gridType.blockCols
        )
        if (conflicts.isNotEmpty()) return

        // Verify with solution or valid sudoku constraints
        _isGameWon.value = true
        _isTimerRunning.value = false
        timerJob?.cancel()

        // Calculate score breakdown
        val hintsUsed = 3 - _hintsRemaining.value
        val scoreResult = BengaliWordPuzzles.calculateVictoryScore(
            gridType = puzzle.gridType,
            elapsedSeconds = _elapsedSeconds.value,
            mistakes = _mistakesCount.value,
            hintsUsed = hintsUsed
        )

        // Record victory in preferences & check badges
        val newBadges = preferences.recordGameWon(
            gridType = puzzle.gridType,
            timeSeconds = _elapsedSeconds.value,
            mistakes = _mistakesCount.value,
            hintsUsed = hintsUsed,
            pointsEarned = scoreResult.totalPoints
        )

        _latestScoreResult.value = scoreResult.copy(newBadges = newBadges)

        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        preferences.markDailyCompleted(todayStr)
        refreshStats()
    }

    fun validateManually() {
        val puzzle = _currentPuzzle.value ?: return
        val size = puzzle.gridType.size
        val current = _cells.value
        val intBoard = current.map { row -> row.map { it.value } }

        if (!BengaliWordPuzzles.isBoardComplete(intBoard, size)) {
            _validationMessage.value = "দয়া করে সব ফাঁকা ঘর পূরণ করুন!"
            return
        }

        val conflicts = BengaliWordPuzzles.checkCellConflicts(
            intBoard,
            size,
            puzzle.gridType.blockRows,
            puzzle.gridType.blockCols
        )
        if (conflicts.isNotEmpty()) {
            _validationMessage.value = "কিছু বর্ণে পুনরাবৃত্তি রয়েছে, লাল ঘরগুলো সংশোধন করুন।"
            recomputeConflicts()
            return
        }

        checkVictory()
    }

    fun shareVictory(context: Context) {
        val puzzle = _currentPuzzle.value ?: return
        val score = _latestScoreResult.value?.totalPoints ?: 250
        val text = BengaliWordPuzzles.buildShareVictoryMessage(
            puzzleTitle = puzzle.gridType.titleBn,
            elapsedSeconds = _elapsedSeconds.value,
            pointsEarned = score,
            totalScore = _stats.value.totalScore,
            streak = _stats.value.currentStreak,
            secretWord = puzzle.secretWord
        )
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "বর্ণ-সুডোকু ফলাফল শেয়ার করুন")
        context.startActivity(shareIntent)
    }

    fun shareChallenge(context: Context) {
        val rank = PlayerRank.getRankForXp(_stats.value.totalXp)
        val unlockedCount = _badges.value.count { it.isUnlocked }
        val text = BengaliWordPuzzles.buildChallengeFriendMessage(
            rankTitle = rank.titleBn,
            totalScore = _stats.value.totalScore,
            badgesCount = unlockedCount
        )
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "বন্ধুকে বর্ণ-সুডোকু চ্যালেঞ্জ জানান")
        context.startActivity(shareIntent)
    }

    // Word Hunt Methods
    fun setWordHuntLevel(level: WordHuntLevel) {
        _currentWordHuntLevel.value = level
        _wordHuntWords.value = level.targetWords
        _currentComposedWord.value = ""
        _wordHuntMessage.value = null
    }

    fun appendLetterToHunt(letter: String) {
        _currentComposedWord.value += letter
        _wordHuntMessage.value = null
    }

    fun clearHuntWord() {
        _currentComposedWord.value = ""
        _wordHuntMessage.value = null
    }

    fun backspaceHuntWord() {
        val cur = _currentComposedWord.value
        if (cur.isNotEmpty()) {
            _currentComposedWord.value = cur.dropLast(1)
        }
    }

    fun submitHuntWord() {
        val word = _currentComposedWord.value
        if (word.isEmpty()) return

        val list = _wordHuntWords.value
        val matchIndex = list.indexOfFirst { it.word == word }

        if (matchIndex >= 0) {
            val item = list[matchIndex]
            if (item.found) {
                _wordHuntMessage.value = "এই শব্দটি ইতিমধ্যে খুঁজে পেয়েছেন!"
            } else {
                _wordHuntWords.value = list.mapIndexed { idx, w ->
                    if (idx == matchIndex) w.copy(found = true) else w
                }
                _wordHuntMessage.value = "অভিনন্দন! সঠিক শব্দ: ${item.word} (${item.meaning})"

                // Award points for word hunt discovery
                preferences.recordGameWon(
                    gridType = GridType.MINI_4X4,
                    timeSeconds = 0,
                    mistakes = 0,
                    hintsUsed = 0,
                    pointsEarned = 50
                )
                refreshStats()
            }
        } else {
            _wordHuntMessage.value = "শব্দটি তালিকায় নেই, আবার চেষ্টা করুন।"
        }
        _currentComposedWord.value = ""
    }
}
