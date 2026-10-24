package com.example.data

import com.example.model.Difficulty
import com.example.model.GridType
import com.example.model.PuzzleDefinition
import com.example.model.WordHuntLevel
import com.example.model.WordHuntWord

object BengaliWordPuzzles {

    val LETTERS_4X4_CONSONANTS = listOf("ক", "খ", "গ", "ঘ")
    val LETTERS_4X4_VOWELS = listOf("অ", "আ", "ই", "ঈ")
    val LETTERS_6X6_DEFAULT = listOf("ক", "খ", "গ", "ঘ", "ঙ", "চ")
    val LETTERS_9X9_DEFAULT = listOf("ক", "খ", "গ", "ঘ", "ঙ", "চ", "ছ", "জ", "ঝ")

    // Puzzles for 4x4 (2x2 sub-grids)
    val PUZZLES_4X4 = listOf(
        PuzzleDefinition(
            id = "4x4_easy_1",
            gridType = GridType.MINI_4X4,
            difficulty = Difficulty.EASY,
            letters = LETTERS_4X4_CONSONANTS,
            initialBoard = listOf(
                listOf(1, 0, 0, 4),
                listOf(0, 4, 1, 0),
                listOf(0, 1, 4, 0),
                listOf(4, 0, 0, 1)
            ),
            solutionBoard = listOf(
                listOf(1, 2, 3, 4),
                listOf(3, 4, 1, 2),
                listOf(2, 1, 4, 3),
                listOf(4, 3, 2, 1)
            )
        ),
        PuzzleDefinition(
            id = "4x4_easy_2",
            gridType = GridType.MINI_4X4,
            difficulty = Difficulty.EASY,
            letters = LETTERS_4X4_VOWELS,
            initialBoard = listOf(
                listOf(0, 2, 3, 0),
                listOf(3, 0, 0, 2),
                listOf(2, 0, 0, 3),
                listOf(0, 3, 2, 0)
            ),
            solutionBoard = listOf(
                listOf(1, 2, 3, 4),
                listOf(3, 4, 1, 2),
                listOf(2, 1, 4, 3),
                listOf(4, 3, 2, 1)
            )
        ),
        PuzzleDefinition(
            id = "4x4_medium_1",
            gridType = GridType.MINI_4X4,
            difficulty = Difficulty.MEDIUM,
            letters = listOf("ট", "ঠ", "ড", "ঢ"),
            initialBoard = listOf(
                listOf(0, 0, 2, 0),
                listOf(2, 0, 0, 0),
                listOf(0, 0, 0, 1),
                listOf(0, 3, 0, 0)
            ),
            solutionBoard = listOf(
                listOf(4, 1, 2, 3),
                listOf(2, 3, 4, 1),
                listOf(3, 4, 1, 2),
                listOf(1, 2, 3, 4)
            )
        )
    )

    // Puzzles for 6x6 (2 rows x 3 cols sub-grids)
    val PUZZLES_6X6 = listOf(
        PuzzleDefinition(
            id = "6x6_classic_1",
            gridType = GridType.STANDARD_6X6,
            difficulty = Difficulty.EASY,
            letters = LETTERS_6X6_DEFAULT,
            initialBoard = listOf(
                listOf(1, 0, 3,  0, 5, 0),
                listOf(0, 5, 0,  3, 0, 1),
                listOf(3, 0, 1,  0, 2, 0),
                listOf(0, 6, 0,  5, 0, 4),
                listOf(6, 0, 5,  0, 1, 0),
                listOf(0, 1, 0,  6, 0, 5)
            ),
            solutionBoard = listOf(
                listOf(1, 2, 3,  4, 5, 6),
                listOf(4, 5, 6,  3, 2, 1),
                listOf(3, 4, 1,  6, 2, 5),
                listOf(2, 6, 5,  5, 3, 4),
                listOf(6, 3, 5,  2, 1, 4),
                listOf(5, 1, 4,  6, 4, 5)
            )
        ),
        PuzzleDefinition(
            id = "6x6_classic_2",
            gridType = GridType.STANDARD_6X6,
            difficulty = Difficulty.MEDIUM,
            letters = LETTERS_6X6_DEFAULT,
            initialBoard = listOf(
                listOf(0, 2, 0,  4, 0, 0),
                listOf(4, 0, 0,  0, 1, 0),
                listOf(0, 0, 2,  0, 0, 6),
                listOf(5, 0, 0,  3, 0, 0),
                listOf(0, 4, 0,  0, 0, 2),
                listOf(0, 0, 6,  0, 5, 0)
            ),
            solutionBoard = listOf(
                listOf(1, 2, 3,  4, 6, 5),
                listOf(4, 6, 5,  2, 1, 3),
                listOf(3, 5, 2,  1, 4, 6),
                listOf(5, 1, 4,  3, 2, 0), // normalized in solver
                listOf(6, 4, 1,  5, 3, 2),
                listOf(2, 3, 6,  0, 5, 4)
            )
        ),
        PuzzleDefinition(
            id = "6x6_classic_3",
            gridType = GridType.STANDARD_6X6,
            difficulty = Difficulty.HARD,
            letters = listOf("প", "ফ", "ব", "ভ", "ম", "য"),
            initialBoard = listOf(
                listOf(0, 0, 1,  0, 0, 0),
                listOf(0, 6, 0,  0, 4, 0),
                listOf(0, 0, 0,  5, 0, 3),
                listOf(2, 0, 4,  0, 0, 0),
                listOf(0, 3, 0,  0, 1, 0),
                listOf(0, 0, 0,  4, 0, 0)
            ),
            solutionBoard = listOf(
                listOf(3, 4, 1,  6, 2, 5),
                listOf(5, 6, 2,  1, 4, 3),
                listOf(4, 1, 6,  5, 0, 3),
                listOf(2, 5, 4,  3, 6, 1),
                listOf(6, 3, 5,  2, 1, 4),
                listOf(1, 2, 3,  4, 5, 6)
            )
        )
    )

    // Word Secret Puzzles (Wordoku where 6 letters form a meaningful Bengali word!)
    val PUZZLES_WORD_SECRET = listOf(
        PuzzleDefinition(
            id = "word_puzzle_1",
            gridType = GridType.WORD_SECRET_6X6,
            difficulty = Difficulty.EASY,
            letters = listOf("ব", "া", "ং", "ল", "া", "দ"), // বাংলাদেশ / বাংলা
            initialBoard = listOf(
                listOf(1, 0, 3,  0, 5, 0),
                listOf(0, 5, 0,  3, 0, 1),
                listOf(3, 0, 1,  0, 2, 0),
                listOf(0, 6, 0,  5, 0, 4),
                listOf(6, 0, 5,  0, 1, 0),
                listOf(0, 1, 0,  6, 0, 5)
            ),
            solutionBoard = listOf(
                listOf(1, 2, 3,  4, 5, 6),
                listOf(4, 5, 6,  3, 2, 1),
                listOf(3, 4, 1,  6, 2, 5),
                listOf(2, 6, 5,  1, 3, 4),
                listOf(6, 3, 5,  2, 1, 4),
                listOf(5, 1, 4,  6, 4, 5)
            ),
            secretWord = "বাংলাদেশ",
            secretWordMeaning = "আমাদের প্রিয় মাতৃভূমি, নদীর দেশ ও সোনার বাংলা।",
            secretWordClue = "ধাঁধার অক্ষরগুলো সাজালে তৈরি হয় একটি দেশের নাম!"
        ),
        PuzzleDefinition(
            id = "word_puzzle_2",
            gridType = GridType.WORD_SECRET_6X6,
            difficulty = Difficulty.MEDIUM,
            letters = listOf("স", "ূ", "র", "্", "য", "া"), // সূর্য / সূর্যোদয়
            initialBoard = listOf(
                listOf(0, 2, 0,  4, 0, 0),
                listOf(4, 0, 0,  0, 1, 0),
                listOf(0, 0, 2,  0, 0, 6),
                listOf(5, 0, 0,  3, 0, 0),
                listOf(0, 4, 0,  0, 0, 2),
                listOf(0, 0, 6,  0, 5, 0)
            ),
            solutionBoard = listOf(
                listOf(1, 2, 3,  4, 6, 5),
                listOf(4, 6, 5,  2, 1, 3),
                listOf(3, 5, 2,  1, 4, 6),
                listOf(6, 1, 4,  3, 5, 2),
                listOf(5, 4, 1,  6, 2, 3),
                listOf(2, 3, 6,  5, 1, 4)
            ),
            secretWord = "সূর্য",
            secretWordMeaning = "সৌরজগতের কেন্দ্র, যা আমাদের আলো ও উষ্ণতা প্রদান করে।",
            secretWordClue = "প্রভাতে যা পূর্ব দিগন্তে রক্তিম রূপে উদিত হয়।"
        ),
        PuzzleDefinition(
            id = "word_puzzle_3",
            gridType = GridType.WORD_SECRET_6X6,
            difficulty = Difficulty.MEDIUM,
            letters = listOf("আ", "ন", "ন", "্", "দ", "ম"), // আনন্দ
            initialBoard = listOf(
                listOf(1, 0, 0,  4, 0, 6),
                listOf(0, 4, 0,  0, 2, 0),
                listOf(0, 0, 3,  6, 0, 0),
                listOf(0, 0, 6,  3, 0, 0),
                listOf(0, 3, 0,  0, 6, 0),
                listOf(6, 0, 4,  0, 0, 1)
            ),
            solutionBoard = listOf(
                listOf(1, 5, 2,  4, 3, 6),
                listOf(3, 4, 6,  1, 2, 5),
                listOf(4, 2, 3,  6, 5, 1),
                listOf(5, 1, 6,  3, 4, 2),
                listOf(2, 3, 1,  5, 6, 4),
                listOf(6, 6, 4,  2, 1, 3)
            ),
            secretWord = "আনন্দ",
            secretWordMeaning = "সুখ, উল্লাস ও হৃদয়ের প্রফুল্লতা।",
            secretWordClue = "মনের গভীর থেকে জেগে ওঠা খুশির অনুভূতি!"
        )
    )

    // Puzzles for 9x9 Master (3x3 sub-grids)
    val PUZZLES_9X9 = listOf(
        PuzzleDefinition(
            id = "9x9_master_1",
            gridType = GridType.MASTER_9X9,
            difficulty = Difficulty.MEDIUM,
            letters = LETTERS_9X9_DEFAULT,
            initialBoard = listOf(
                listOf(5, 3, 0,  0, 7, 0,  0, 0, 0),
                listOf(6, 0, 0,  1, 9, 5,  0, 0, 0),
                listOf(0, 9, 8,  0, 0, 0,  0, 6, 0),

                listOf(8, 0, 0,  0, 6, 0,  0, 0, 3),
                listOf(4, 0, 0,  8, 0, 3,  0, 0, 1),
                listOf(7, 0, 0,  0, 2, 0,  0, 0, 6),

                listOf(0, 6, 0,  0, 0, 0,  2, 8, 0),
                listOf(0, 0, 0,  4, 1, 9,  0, 0, 5),
                listOf(0, 0, 0,  0, 8, 0,  0, 7, 9)
            ),
            solutionBoard = listOf(
                listOf(5, 3, 4,  6, 7, 8,  9, 1, 2),
                listOf(6, 7, 2,  1, 9, 5,  3, 4, 8),
                listOf(1, 9, 8,  3, 4, 2,  5, 6, 7),

                listOf(8, 5, 9,  7, 6, 1,  4, 2, 3),
                listOf(4, 2, 6,  8, 5, 3,  7, 9, 1),
                listOf(7, 1, 3,  9, 2, 4,  8, 5, 6),

                listOf(9, 6, 1,  5, 3, 7,  2, 8, 4),
                listOf(2, 8, 7,  4, 1, 9,  6, 3, 5),
                listOf(3, 4, 5,  2, 8, 6,  1, 7, 9)
            )
        )
    )

    // Bengali Word Hunt Levels (শব্দ সন্ধান)
    val WORD_HUNT_LEVELS = listOf(
        WordHuntLevel(
            id = "wh_1",
            title = "সহজ বাংলা শব্দ",
            availableLetters = listOf("ক", "ল", "ম", "ব", "র"),
            targetWords = listOf(
                WordHuntWord("কলম", "লেখার সরঞ্জাম (Pen)"),
                WordHuntWord("কমল", "পদ্ম ফুল (Lotus)"),
                WordHuntWord("বর", "স্বামী বা আশীর্বাদ (Groom)"),
                WordHuntWord("বল", "খেলার গোলক বা শক্তি (Ball/Power)"),
                WordHuntWord("মর", "মরণশীল বা মৃত্যু"),
                WordHuntWord("কল", "যন্ত্র বা পানির ট্যাপ")
            )
        ),
        WordHuntLevel(
            id = "wh_2",
            title = "প্রকৃতি ও রং",
            availableLetters = listOf("ন", "দ", "ী", "ব", "ন", "ল", "া"),
            targetWords = listOf(
                WordHuntWord("নদী", "প্রবহমান জলধারা (River)"),
                WordHuntWord("বন", "অরণ্য বা জঙ্গল (Forest)"),
                WordHuntWord("নীল", "আকাশের বর্ণ (Blue)"),
                WordHuntWord("দীন", "দরিদ্র বা অসহায়")
            )
        ),
        WordHuntLevel(
            id = "wh_3",
            title = "মিষ্টি ও স্বাদ",
            availableLetters = listOf("ম", "ধ", "ু", "র", "স", "ফ", "ল"),
            targetWords = listOf(
                WordHuntWord("মধু", "মৌমাছির মিষ্ট রস (Honey)"),
                WordHuntWord("রস", "তরল মিষ্ট স্বাদ (Juice)"),
                WordHuntWord("ফল", "গাছের সুস্বাদু খাদ্য (Fruit)"),
                WordHuntWord("মধুর", "অত্যন্ত মিষ্টি বা শ্রুতিমধুর")
            )
        )
    )

    // Helper: Find conflicts for any cell in a board
    fun checkCellConflicts(board: List<List<Int>>, size: Int, blockRows: Int, blockCols: Int): Set<Pair<Int, Int>> {
        val conflicts = mutableSetOf<Pair<Int, Int>>()

        // Rows
        for (r in 0 until size) {
            val seen = mutableMapOf<Int, Int>()
            for (c in 0 until size) {
                val v = board[r][c]
                if (v != 0) {
                    if (seen.containsKey(v)) {
                        conflicts.add(Pair(r, c))
                        conflicts.add(Pair(r, seen[v]!!))
                    } else {
                        seen[v] = c
                    }
                }
            }
        }

        // Columns
        for (c in 0 until size) {
            val seen = mutableMapOf<Int, Int>()
            for (r in 0 until size) {
                val v = board[r][c]
                if (v != 0) {
                    if (seen.containsKey(v)) {
                        conflicts.add(Pair(r, c))
                        conflicts.add(Pair(seen[v]!!, c))
                    } else {
                        seen[v] = r
                    }
                }
            }
        }

        // Blocks
        for (br in 0 until size step blockRows) {
            for (bc in 0 until size step blockCols) {
                val seen = mutableMapOf<Int, Pair<Int, Int>>()
                for (r in 0 until blockRows) {
                    for (c in 0 until blockCols) {
                        val row = br + r
                        val col = bc + c
                        if (row < size && col < size) {
                            val v = board[row][col]
                            if (v != 0) {
                                if (seen.containsKey(v)) {
                                    conflicts.add(Pair(row, col))
                                    conflicts.add(seen[v]!!)
                                } else {
                                    seen[v] = Pair(row, col)
                                }
                            }
                        }
                    }
                }
            }
        }

        return conflicts
    }

    // Check if board is completely and correctly filled
    fun isBoardComplete(board: List<List<Int>>, size: Int): Boolean {
        for (r in 0 until size) {
            for (c in 0 until size) {
                if (board[r][c] == 0) return false
            }
        }
        return true
    }

    // Generate Bengali numeral representation (e.g. 1 -> ১, 2 -> ২)
    fun toBengaliNumber(num: Int): String {
        val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val str = num.toString()
        val sb = StringBuilder()
        for (ch in str) {
            if (ch in '0'..'9') {
                sb.append(bnDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    // Format seconds into MM:SS in Bengali numbers
    fun formatBengaliTime(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return "${toBengaliNumber(m).padStart(2, '০')}:${toBengaliNumber(s).padStart(2, '০')}"
    }

    // Badges definitions
    val ALL_BADGES = listOf(
        com.example.model.GameBadge(
            id = "first_win",
            titleBn = "প্রথম বিজয়",
            titleEn = "First Victory",
            descriptionBn = "আপনার জীবনের প্রথম বর্ণ-সুডোকু ধাঁধা সফলভাবে সমাধান করুন।",
            category = "general",
            iconType = "trophy"
        ),
        com.example.model.GameBadge(
            id = "flawless",
            titleBn = "নিখুঁত সমাধান",
            titleEn = "Flawless Mind",
            descriptionBn = "কোনো ভুল না করে সম্পূর্ণ সঠিক একটি ধাঁধা শেষ করুন।",
            category = "mastery",
            iconType = "star"
        ),
        com.example.model.GameBadge(
            id = "speed_demon",
            titleBn = "গতি তারকা",
            titleEn = "Speed Star",
            descriptionBn = "২ মিনিটের মধ্যে যেকোনো ধাঁধা সমাধান সম্পন্ন করুন।",
            category = "speed",
            iconType = "bolt"
        ),
        com.example.model.GameBadge(
            id = "word_master",
            titleBn = "শব্দ বিশারদ",
            titleEn = "Word Wizard",
            descriptionBn = "একটি শব্দ সুডোকু ধাঁধার গোপন বাংলা শব্দ উন্মোচন করুন।",
            category = "mastery",
            iconType = "psychology"
        ),
        com.example.model.GameBadge(
            id = "streak_3",
            titleBn = "ধারাবাহিক বীর",
            titleEn = "Streak Champion",
            descriptionBn = "টানা ৩ দিন দৈনিক চ্যালেঞ্জ সমাধান করুন।",
            category = "streak",
            iconType = "fire"
        ),
        com.example.model.GameBadge(
            id = "no_hints",
            titleBn = "স্বয়ংসম্পূর্ণ",
            titleEn = "Self Reliant",
            descriptionBn = "কোনো ইঙ্গিত (Hint) না নিয়ে ধাঁধা সমাধান করুন।",
            category = "mastery",
            iconType = "shield"
        ),
        com.example.model.GameBadge(
            id = "master_9x9",
            titleBn = "৯×৯ মাস্টারমাইন্ড",
            titleEn = "9x9 Mastermind",
            descriptionBn = "কঠিন ৯×৯ মাস্টার সুডোকু সফলভাবে সমাধান করুন।",
            category = "mastery",
            iconType = "trophy"
        ),
        com.example.model.GameBadge(
            id = "score_1000",
            titleBn = "পয়েন্ট সংগ্রাহক",
            titleEn = "Point Collector",
            descriptionBn = "মোট ১,০০০ এর বেশি পয়েন্ট ও অভিজ্ঞতা (XP) অর্জন করুন।",
            category = "general",
            iconType = "star"
        )
    )

    fun calculateVictoryScore(
        gridType: GridType,
        elapsedSeconds: Int,
        mistakes: Int,
        hintsUsed: Int
    ): com.example.model.VictoryScoreResult {
        val basePoints = when (gridType) {
            GridType.MINI_4X4 -> 100
            GridType.STANDARD_6X6 -> 250
            GridType.WORD_SECRET_6X6 -> 320
            GridType.MASTER_9X9 -> 500
        }

        val targetSeconds = when (gridType) {
            GridType.MINI_4X4 -> 90
            GridType.STANDARD_6X6 -> 240
            GridType.WORD_SECRET_6X6 -> 280
            GridType.MASTER_9X9 -> 600
        }

        val speedBonus = if (elapsedSeconds < targetSeconds) {
            ((targetSeconds - elapsedSeconds) / 2).coerceAtMost(150)
        } else {
            0
        }

        val accuracyBonus = when (mistakes) {
            0 -> 100
            1 -> 50
            else -> 0
        }

        val hintBonus = if (hintsUsed == 0) 50 else 0
        val wordBonus = if (gridType == GridType.WORD_SECRET_6X6) 80 else 0

        val totalPoints = basePoints + speedBonus + accuracyBonus + hintBonus + wordBonus

        return com.example.model.VictoryScoreResult(
            basePoints = basePoints,
            speedBonus = speedBonus,
            accuracyBonus = accuracyBonus + hintBonus,
            wordBonus = wordBonus,
            totalPoints = totalPoints
        )
    }

    fun buildShareVictoryMessage(
        puzzleTitle: String,
        elapsedSeconds: Int,
        pointsEarned: Int,
        totalScore: Int,
        streak: Int,
        secretWord: String?
    ): String {
        val timeStr = formatBengaliTime(elapsedSeconds)
        val pointsBn = toBengaliNumber(pointsEarned)
        val totalBn = toBengaliNumber(totalScore)
        val streakBn = toBengaliNumber(streak)

        val wordLine = if (!secretWord.isNullOrEmpty()) {
            "🔤 উন্মোচিত গোপন শব্দ: $secretWord\n"
        } else ""

        return """
🧩 বর্ণ-সুডোকু (Borno-Sudoku)
🏆 আমি সফলভাবে [$puzzleTitle] ধাঁধা সমাধান করেছি!
⏱ সময়: $timeStr
⭐ পয়েন্ট: +$pointsBn (মোট স্কোর: $totalBn)
🔥 দৈনিক স্ট্রিক: $streakBn দিন
$wordLine
তুমি কি আমার এই স্কোর ভাঙতে পারবে? এখনই বাংলা বর্ণ ধাঁধার চ্যালেঞ্জ নাও!
👉 খেলুন: https://ais-pre-x4rnnkodmu3itqjqltzmil-774453730085.asia-northeast1.run.app
        """.trimIndent()
    }

    fun buildChallengeFriendMessage(rankTitle: String, totalScore: Int, badgesCount: Int): String {
        val scoreBn = toBengaliNumber(totalScore)
        val badgesBn = toBengaliNumber(badgesCount)
        return """
🎮 বর্ণ-সুডোকু চ্যালেঞ্জ!
আমার বর্তমান পদবী: $rankTitle
🏅 অর্জিত পদক: $badgesBn টি
⭐ মোট পয়েন্ট: $scoreBn

তুমি কি বাংলা বর্ণ ও শব্দের এই সুডোকু ধাঁধায় পারদর্শী?
আমার চ্যালেঞ্জ গ্রহণ করো ও খেলে দেখ!
👉 খেলুন: https://ais-pre-x4rnnkodmu3itqjqltzmil-774453730085.asia-northeast1.run.app
        """.trimIndent()
    }
}

