package com.hpceen.tictactoe.help

import android.widget.ImageButton
import androidx.lifecycle.MutableLiveData
import com.hpceen.tictactoe.R
import com.hpceen.tictactoe.states.State

//Клетка малого поля (ячейка)
//Конструктор принимает ImageButton
class Cell(var button: ImageButton) {
    //Состояние ячейки (по умолчанию "Ничего")
    var state: MutableLiveData<State> = MutableLiveData()

    init {
        state.observeForever {
            changeImage(it)
            button.isClickable = false
        }
    }

    fun changeButton(newButton: ImageButton) {
        this.button = newButton
        if (state.isInitialized) {
            button.isClickable = false
            changeImage(state.value!!)
        }
    }

    //Изменение изображения ImageButton в зависимости от хода
    fun changeImage(cellState: State) {
        button.setImageResource(if (cellState == State.X) R.drawable.cross else R.drawable.circle)
    }

}