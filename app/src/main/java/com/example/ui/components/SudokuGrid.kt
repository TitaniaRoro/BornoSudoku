package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GridType
import com.example.model.SudokuCell
import com.example.ui.theme.BoardBorderColor
import com.example.ui.theme.CellBorderColor
import com.example.ui.theme.CellConflictBackground
import com.example.ui.theme.CellConflictText
import com.example.ui.theme.CellGivenBackground
import com.example.ui.theme.CellHighlightBackground
import com.example.ui.theme.CellSameNumberBackground
import com.example.ui.theme.CellSelectedBackground
import com.example.ui.theme.CellUserBackground
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldTertiary
import com.example.ui.theme.NoteTextColor

@Composable
fun SudokuGrid(
    gridType: GridType,
    letters: List<String>,
    cells: List<List<SudokuCell>>,
    selectedCell: Pair<Int, Int>?,
    highlightSameLetter: Boolean,
    onCellClick: (row: Int, col: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val size = gridType.size
    val blockRows = gridType.blockRows
    val blockCols = gridType.blockCols

    val selectedValue = selectedCell?.let { (r, c) ->
        cells.getOrNull(r)?.getOrNull(c)?.value
    } ?: 0

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(BoardBorderColor)
            .border(3.dp, BoardBorderColor, RoundedCornerShape(12.dp))
            .padding(1.5.dp)
            .testTag("sudoku_grid")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            for (r in 0 until size) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (c in 0 until size) {
                        val cell = cells.getOrNull(r)?.getOrNull(c) ?: SudokuCell(r, c)
                        val isSelected = selectedCell?.first == r && selectedCell?.second == c
                        val isInSelectedRowOrCol = selectedCell != null && (selectedCell.first == r || selectedCell.second == c)
                        val isInSelectedBlock = selectedCell != null &&
                                (r / blockRows == selectedCell.first / blockRows) &&
                                (c / blockCols == selectedCell.second / blockCols)
                        val isSameValue = highlightSameLetter && selectedValue != 0 && cell.value == selectedValue

                        // Border thickness for sub-grid blocks
                        val isBlockBottom = (r + 1) % blockRows == 0 && (r + 1) < size
                        val isBlockEnd = (c + 1) % blockCols == 0 && (c + 1) < size

                        SudokuCellView(
                            cell = cell,
                            letters = letters,
                            isSelected = isSelected,
                            isHighlighted = (isInSelectedRowOrCol || isInSelectedBlock) && !isSelected,
                            isSameValue = isSameValue && !isSelected,
                            isBlockBottom = isBlockBottom,
                            isBlockEnd = isBlockEnd,
                            onClick = { onCellClick(r, c) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .testTag("cell_${r}_${c}")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SudokuCellView(
    cell: SudokuCell,
    letters: List<String>,
    isSelected: Boolean,
    isHighlighted: Boolean,
    isSameValue: Boolean,
    isBlockBottom: Boolean,
    isBlockEnd: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val targetBg = when {
        cell.isConflict -> CellConflictBackground
        isSelected -> CellSelectedBackground
        isSameValue -> CellSameNumberBackground
        isHighlighted -> CellHighlightBackground
        cell.isGiven -> CellGivenBackground
        else -> CellUserBackground
    }

    val animatedBg by animateColorAsState(targetValue = targetBg, label = "cell_bg")

    val textColor = when {
        cell.isConflict -> CellConflictText
        cell.isGiven -> Color(0xFF1E293B)
        cell.isHinted -> EmeraldTertiary
        else -> EmeraldPrimary
    }

    val letterDisplay = if (cell.value in 1..letters.size) {
        letters[cell.value - 1]
    } else {
        ""
    }

    Box(
        modifier = modifier
            .background(animatedBg)
            .border(
                width = 0.5.dp,
                color = CellBorderColor
            )
            .let { mod ->
                if (isBlockEnd) {
                    mod.border(width = 2.dp, color = BoardBorderColor)
                } else mod
            }
            .let { mod ->
                if (isBlockBottom) {
                    mod.border(width = 2.dp, color = BoardBorderColor)
                } else mod
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (letterDisplay.isNotEmpty()) {
            Text(
                text = letterDisplay,
                color = textColor,
                fontSize = if (letters.size <= 4) 28.sp else if (letters.size <= 6) 24.sp else 18.sp,
                fontWeight = if (cell.isGiven) FontWeight.Bold else FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        } else if (cell.notes.isNotEmpty()) {
            // Notes candidate mini-grid
            CellNotesGrid(notes = cell.notes, letters = letters)
        }
    }
}

@Composable
private fun CellNotesGrid(
    notes: Set<Int>,
    letters: List<String>
) {
    val count = letters.size
    val cols = if (count <= 4) 2 else 3

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        for (row in 0 until (count + cols - 1) / cols) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0 until cols) {
                    val idx = row * cols + col + 1
                    if (idx <= count) {
                        val text = if (notes.contains(idx)) letters[idx - 1] else " "
                        Text(
                            text = text,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Normal,
                            color = NoteTextColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
