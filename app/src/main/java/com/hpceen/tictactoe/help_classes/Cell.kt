package com.hpceen.tictactoe.help_classes

import android.widget.ImageButton
import com.hpceen.tictactoe.R
import com.hpceen.tictactoe.states.CellState
import com.hpceen.tictactoe.states.Turn

class Cell(val button: ImageButton) {

    var state: CellState = CellState.Nothing

    fun changeImage(currentTurn: Turn) {
        button.setImageResource(if (currentTurn == Turn.X) R.drawable.cross else R.drawable.circle)
    }

}