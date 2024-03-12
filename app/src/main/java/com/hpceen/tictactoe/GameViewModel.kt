package com.hpceen.tictactoe

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hpceen.tictactoe.help_classes.Cluster
import com.hpceen.tictactoe.states.GameState
import com.hpceen.tictactoe.states.Turn

class GameViewModel : ViewModel() {
    //Игровое поле
    var gameField: MutableLiveData<MutableList<Cluster>> = MutableLiveData(mutableListOf())
    //Состояние игры
    var gameState: MutableLiveData<GameState> = MutableLiveData(GameState.Nothing)
    //Текущий ход
    var currentTurn: MutableLiveData<Turn> = MutableLiveData(Turn.X)
    //Доступные для хода кластеры
    var allowedClusters: MutableLiveData<MutableSet<Int>> = MutableLiveData((0..8).toMutableSet())
    //Завершенные кластеры
    var finishedClusters: MutableLiveData<MutableSet<Int>> = MutableLiveData(mutableSetOf())
}