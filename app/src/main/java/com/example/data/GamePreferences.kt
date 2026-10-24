package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.GameBadge
import com.example.model.GameStatsData
import com.example.model.GridType

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("borno_sudoku_prefs", Context.MODE_PRIVATE)

    fun getStats(): GameStatsData {
        return GameStatsData(
            gamesPlayed = prefs.getInt("games_played", 0),
            gamesWon = prefs.getInt("games_won", 0),
            currentStreak = prefs.getInt("current_streak", 0),
            bestStreak = prefs.getInt("best_streak", 0),
            bestTimeSeconds4x4 = prefs.getInt("best_time_4x4", 0),
            bestTimeSeconds6x6 = prefs.getInt("best_time_6x6", 0),
            bestTimeSeconds9x9 = prefs.getInt("best_time_9x9", 0),
            wordPuzzlesSolved = prefs.getInt("word_puzzles_solved", 0),
            totalScore = prefs.getInt("total_score", 0),
            totalXp = prefs.getInt("total_xp", 0)
        )
    }

    fun getUnlockedBadgeIds(): Set<String> {
        return prefs.getStringSet("unlocked_badges", emptySet()) ?: emptySet()
    }

    fun getBadgesWithUnlockStatus(): List<GameBadge> {
        val unlockedIds = getUnlockedBadgeIds()
        return BengaliWordPuzzles.ALL_BADGES.map { badge ->
            badge.copy(isUnlocked = unlockedIds.contains(badge.id))
        }
    }

    fun recordGameWon(
        gridType: GridType,
        timeSeconds: Int,
        mistakes: Int,
        hintsUsed: Int,
        pointsEarned: Int
    ): List<GameBadge> {
        val current = getStats()
        val newPlayed = current.gamesPlayed + 1
        val newWon = current.gamesWon + 1
        val newStreak = current.currentStreak + 1
        val newBestStreak = maxOf(current.bestStreak, newStreak)
        val newTotalScore = current.totalScore + pointsEarned
        val newTotalXp = current.totalXp + pointsEarned
        val isWordPuzzle = gridType == GridType.WORD_SECRET_6X6
        val newWordSolved = if (isWordPuzzle) current.wordPuzzlesSolved + 1 else current.wordPuzzlesSolved

        val editor = prefs.edit()
            .putInt("games_played", newPlayed)
            .putInt("games_won", newWon)
            .putInt("current_streak", newStreak)
            .putInt("best_streak", newBestStreak)
            .putInt("total_score", newTotalScore)
            .putInt("total_xp", newTotalXp)
            .putInt("word_puzzles_solved", newWordSolved)

        when (gridType.size) {
            4 -> {
                if (current.bestTimeSeconds4x4 == 0 || timeSeconds < current.bestTimeSeconds4x4) {
                    editor.putInt("best_time_4x4", timeSeconds)
                }
            }
            6 -> {
                if (current.bestTimeSeconds6x6 == 0 || timeSeconds < current.bestTimeSeconds6x6) {
                    editor.putInt("best_time_6x6", timeSeconds)
                }
            }
            9 -> {
                if (current.bestTimeSeconds9x9 == 0 || timeSeconds < current.bestTimeSeconds9x9) {
                    editor.putInt("best_time_9x9", timeSeconds)
                }
            }
        }

        // Check for new badges
        val currentUnlocked = getUnlockedBadgeIds().toMutableSet()
        val newlyUnlocked = mutableListOf<GameBadge>()

        fun tryUnlock(id: String) {
            if (!currentUnlocked.contains(id)) {
                currentUnlocked.add(id)
                BengaliWordPuzzles.ALL_BADGES.find { it.id == id }?.let {
                    newlyUnlocked.add(it.copy(isUnlocked = true))
                }
            }
        }

        // 1. First Win
        tryUnlock("first_win")

        // 2. Flawless
        if (mistakes == 0) {
            tryUnlock("flawless")
        }

        // 3. Speed Demon (< 120s)
        if (timeSeconds < 120) {
            tryUnlock("speed_demon")
        }

        // 4. Word Master
        if (isWordPuzzle) {
            tryUnlock("word_master")
        }

        // 5. Streak 3
        if (newStreak >= 3) {
            tryUnlock("streak_3")
        }

        // 6. No hints
        if (hintsUsed == 0) {
            tryUnlock("no_hints")
        }

        // 7. Master 9x9
        if (gridType == GridType.MASTER_9X9) {
            tryUnlock("master_9x9")
        }

        // 8. Score 1000
        if (newTotalScore >= 1000) {
            tryUnlock("score_1000")
        }

        editor.putStringSet("unlocked_badges", currentUnlocked)
        editor.apply()

        return newlyUnlocked
    }

    fun isAutoCheckEnabled(): Boolean = prefs.getBoolean("auto_check_conflicts", true)
    fun setAutoCheckEnabled(enabled: Boolean) = prefs.edit().putBoolean("auto_check_conflicts", enabled).apply()

    fun isHighlightSameNumbersEnabled(): Boolean = prefs.getBoolean("highlight_same_numbers", true)
    fun setHighlightSameNumbersEnabled(enabled: Boolean) = prefs.edit().putBoolean("highlight_same_numbers", enabled).apply()

    fun isDailyCompletedToday(todayKey: String): Boolean {
        return prefs.getBoolean("daily_completed_$todayKey", false)
    }

    fun markDailyCompleted(todayKey: String) {
        prefs.edit().putBoolean("daily_completed_$todayKey", true).apply()
    }
}
