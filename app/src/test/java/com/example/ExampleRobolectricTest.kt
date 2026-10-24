package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.BengaliWordPuzzles
import com.example.model.GridType
import com.example.model.PlayerRank
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Borno Sudoku", appName)
  }

  @Test
  fun `bengali numerals conversion`() {
    assertEquals("১", BengaliWordPuzzles.toBengaliNumber(1))
    assertEquals("১২৩", BengaliWordPuzzles.toBengaliNumber(123))
    assertEquals("০১:৪৫", BengaliWordPuzzles.formatBengaliTime(105))
  }

  @Test
  fun `sudoku board completion validator`() {
    val completeBoard = listOf(
      listOf(1, 2, 3, 4),
      listOf(3, 4, 1, 2),
      listOf(2, 1, 4, 3),
      listOf(4, 3, 2, 1)
    )
    assertTrue(BengaliWordPuzzles.isBoardComplete(completeBoard, 4))
    val conflicts = BengaliWordPuzzles.checkCellConflicts(completeBoard, 4, 2, 2)
    assertTrue(conflicts.isEmpty())
  }

  @Test
  fun `victory score calculation`() {
    val score = BengaliWordPuzzles.calculateVictoryScore(
      gridType = GridType.STANDARD_6X6,
      elapsedSeconds = 90,
      mistakes = 0,
      hintsUsed = 0
    )
    assertTrue(score.basePoints == 250)
    assertTrue(score.accuracyBonus >= 100)
    assertTrue(score.totalPoints > 350)
  }

  @Test
  fun `badges definitions and ranks`() {
    assertTrue(BengaliWordPuzzles.ALL_BADGES.isNotEmpty())
    val rank1 = PlayerRank.getRankForXp(150)
    assertEquals(1, rank1.level)
    val rank2 = PlayerRank.getRankForXp(500)
    assertEquals(2, rank2.level)
  }

  @Test
  fun `share message format`() {
    val shareMsg = BengaliWordPuzzles.buildShareVictoryMessage(
      puzzleTitle = "৬×৬ ক্লাসিক সুডোকু",
      elapsedSeconds = 120,
      pointsEarned = 350,
      totalScore = 1200,
      streak = 3,
      secretWord = "বাংলাদেশ"
    )
    assertTrue(shareMsg.contains("বর্ণ-সুডোকু"))
    assertTrue(shareMsg.contains("বাংলাদেশ"))
    assertTrue(shareMsg.contains("পয়েন্ট"))
  }
}
