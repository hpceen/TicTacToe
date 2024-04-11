package com.hpceen.tictactoe

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hpceen.tictactoe.help_classes.Cluster
import com.hpceen.tictactoe.states.State

class GameViewModel : ViewModel() {
    //Игровое поле
    var gameField: MutableList<Cluster> = mutableListOf()

    //Состояние игры
    var gameState: MutableLiveData<State?> = MutableLiveData()

    //Текущий ход
    var currentTurn: MutableLiveData<State> = MutableLiveData()

    //Доступные для хода кластеры
    var allowedClusters: MutableLiveData<Set<Int>> = MutableLiveData((0..8).toSet())

    //Завершенные кластеры
    var finishedClusters: MutableSet<Int> = mutableSetOf()
}