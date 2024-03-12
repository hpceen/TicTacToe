package com.hpceen.tictactoe.help_classes

import android.widget.ImageButton
import com.hpceen.tictactoe.R
import com.hpceen.tictactoe.states.CellState
import com.hpceen.tictactoe.states.Turn

//Клетка малого поля (ячейка)
//Конструктор принимает ImageButton
class Cell(val button: ImageButton) {

    //Состояние ячейки (по умолчанию "Ничего")
    var state: CellState = CellState.Nothing

    //Изменение изображения ImageButton в зависимости от хода
    fun changeImage(currentTurn: Turn) {
        button.setImageResource(if (currentTurn == Turn.X) R.drawable.cross else R.drawable.circle)
    }

}