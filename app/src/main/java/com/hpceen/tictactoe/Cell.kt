package com.hpceen.tictactoe

import android.view.View
import android.widget.ImageButton

class Cell(private val button: ImageButton) {

    var state: CellState = CellState.Nothing
        get() = field
        set(value) {
            field = value
        }

    fun changeImage(currentTurn: Turn) {
        button.setImageResource(if (currentTurn == Turn.X) R.drawable.cross else R.drawable.circle)
    }

    fun setOnClickListener(lambda: View.OnClickListener) {
        button.setOnClickListener {lambda}
    }

}