package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BengaliWordPuzzles
import com.example.model.SudokuCell
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.EmeraldTertiary

@Composable
fun BengaliKeypad(
    letters: List<String>,
    cells: List<List<SudokuCell>>,
    isNoteMode: Boolean,
    hintsRemaining: Int,
    onLetterClick: (Int) -> Unit,
    onEraseClick: () -> Unit,
    onToggleNoteMode: () -> Unit,
    onUndoClick: () -> Unit,
    onHintClick: () -> Unit,
    onValidateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalRequiredPerLetter = letters.size // in standard sudoku, each letter appears N times

    // Calculate how many of each letter are placed
    val placementCounts = IntArray(letters.size + 1)
    cells.forEach { row ->
        row.forEach { cell ->
            if (cell.value in 1..letters.size) {
                placementCounts[cell.value]++
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bengali_keypad"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Controls Row: Undo, Erase, Notes, Hint, Validate
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ControlActionButton(
                icon = Icons.Default.Undo,
                label = "পূর্বাবস্থা",
                onClick = onUndoClick,
                testTag = "btn_undo"
            )

            ControlActionButton(
                icon = Icons.Default.Clear,
                label = "মুছুন",
                onClick = onEraseClick,
                testTag = "btn_erase"
            )

            ControlActionButton(
                icon = Icons.Default.Edit,
                label = if (isNoteMode) "টীকা: চালু" else "টীকা",
                isActive = isNoteMode,
                onClick = onToggleNoteMode,
                testTag = "btn_note_toggle"
            )

            ControlActionButton(
                icon = Icons.Default.AutoFixHigh,
                label = "ইঙ্গিত (${BengaliWordPuzzles.toBengaliNumber(hintsRemaining)})",
                onClick = onHintClick,
                testTag = "btn_hint"
            )

            ControlActionButton(
                icon = Icons.Default.Check,
                label = "যাচাই",
                isPrimary = true,
                onClick = onValidateClick,
                testTag = "btn_validate"
            )
        }

        // Bengali Letter Buttons Row(s)
        val chunkCount = if (letters.size <= 6) letters.size else 5
        val chunks = letters.chunked(chunkCount)

        chunks.forEachIndexed { chunkIndex, chunkLetters ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
            ) {
                chunkLetters.forEachIndexed { indexInChunk, letter ->
                    val letterIndex = chunkIndex * chunkCount + indexInChunk + 1
                    val placedCount = placementCounts.getOrElse(letterIndex) { 0 }
                    val isComplete = placedCount >= totalRequiredPerLetter
                    val remaining = maxOf(0, totalRequiredPerLetter - placedCount)

                    LetterKeyButton(
                        letter = letter,
                        remaining = remaining,
                        isComplete = isComplete,
                        onClick = { onLetterClick(letterIndex) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("key_$letter")
                    )
                }
            }
        }
    }
}

@Composable
private fun LetterKeyButton(
    letter: String,
    remaining: Int,
    isComplete: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isComplete) Color(0xFFF1F5F9) else MaterialTheme.colorScheme.surface,
        label = "key_bg"
    )

    val borderColor = if (isComplete) Color(0xFFCBD5E1) else EmeraldSecondary
    val textColor = if (isComplete) Color(0xFF94A3B8) else Color(0xFF0F172A)

    Surface(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (isComplete) 1.dp else 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick),
        color = bgColor,
        shadowElevation = if (isComplete) 0.dp else 2.dp
    ) {
        Box(
            modifier = Modifier.padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )

            // Remaining count badge at top right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .size(16.dp)
                    .background(
                        color = if (isComplete) Color(0xFF10B981) else Color(0xFFE2E8F0),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isComplete) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Complete",
                        tint = Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                } else {
                    Text(
                        text = BengaliWordPuzzles.toBengaliNumber(remaining),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )
                }
            }
        }
    }
}

@Composable
private fun ControlActionButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean = false,
    isPrimary: Boolean = false,
    onClick: () -> Unit,
    testTag: String
) {
    val bg = when {
        isPrimary -> EmeraldPrimary
        isActive -> EmeraldTertiary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when {
        isPrimary || isActive -> Color.White
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(bg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isActive || isPrimary) FontWeight.Bold else FontWeight.Medium,
            color = if (isActive) EmeraldTertiary else MaterialTheme.colorScheme.onBackground
        )
    }
}
