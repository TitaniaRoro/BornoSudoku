package com.example.model

enum class GridType(val size: Int, val blockRows: Int, val blockCols: Int, val titleBn: String, val titleEn: String) {
    MINI_4X4(4, 2, 2, "৪×৪ মিনি", "4×4 Mini"),
    STANDARD_6X6(6, 2, 3, "৬×৬ ক্লাসিক", "6×6 Classic"),
    MASTER_9X9(9, 3, 3, "৯×৯ মাস্টার", "9×9 Master"),
    WORD_SECRET_6X6(6, 2, 3, "শব্দ সুডোকু", "Word Secret")
}

enum class Difficulty(val titleBn: String, val titleEn: String) {
    EASY("সহজ", "Easy"),
    MEDIUM("মাঝারি", "Medium"),
    HARD("কঠিন", "Hard")
}

data class SudokuCell(
    val row: Int,
    val col: Int,
    val value: Int = 0, // 0 = empty, 1..size = letter index
    val isGiven: Boolean = false,
    val notes: Set<Int> = emptySet(),
    val isConflict: Boolean = false,
    val isHinted: Boolean = false
)

data class PuzzleDefinition(
    val id: String,
    val gridType: GridType,
    val difficulty: Difficulty,
    val letters: List<String>, // e.g. ["ক", "খ", "গ", "ঘ", "ঙ", "চ"]
    val initialBoard: List<List<Int>>, // 0 for empty, 1..size
    val solutionBoard: List<List<Int>>,
    val secretWord: String? = null,
    val secretWordMeaning: String? = null,
    val secretWordClue: String? = null
)

data class MoveRecord(
    val row: Int,
    val col: Int,
    val prevValue: Int,
    val newValue: Int,
    val prevNotes: Set<Int>,
    val newNotes: Set<Int>
)

data class WordHuntWord(
    val word: String,
    val meaning: String,
    val found: Boolean = false
)

data class WordHuntLevel(
    val id: String,
    val title: String,
    val availableLetters: List<String>,
    val targetWords: List<WordHuntWord>
)

data class GameStatsData(
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val bestTimeSeconds4x4: Int = 0,
    val bestTimeSeconds6x6: Int = 0,
    val bestTimeSeconds9x9: Int = 0,
    val wordPuzzlesSolved: Int = 0,
    val totalScore: Int = 0,
    val totalXp: Int = 0
)

data class GameBadge(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val descriptionBn: String,
    val category: String, // "general", "speed", "streak", "mastery"
    val iconType: String, // "star", "bolt", "trophy", "shield", "fire", "psychology", "menu_book"
    val isUnlocked: Boolean = false
)

data class VictoryScoreResult(
    val basePoints: Int,
    val speedBonus: Int,
    val accuracyBonus: Int,
    val wordBonus: Int,
    val totalPoints: Int,
    val newBadges: List<GameBadge> = emptyList()
)

data class PlayerRank(
    val level: Int,
    val titleBn: String,
    val minXp: Int,
    val maxXp: Int
) {
    companion object {
        fun getRankForXp(xp: Int): PlayerRank {
            return when {
                xp < 300 -> PlayerRank(1, "শিক্ষানবিস (Novice)", 0, 300)
                xp < 800 -> PlayerRank(2, "বর্ণসন্ধানী (Letter Explorer)", 300, 800)
                xp < 1600 -> PlayerRank(3, "ধাঁধা সাধক (Puzzle Adept)", 800, 1600)
                xp < 3000 -> PlayerRank(4, "সুডোকু পণ্ডিত (Sudoku Scholar)", 1600, 3000)
                xp < 5000 -> PlayerRank(5, "শব্দ জাদুকর (Word Wizard)", 3000, 5000)
                else -> PlayerRank(6, "বর্ণসম্রাট (Wordoku Emperor)", 5000, 10000)
            }
        }
    }
}
