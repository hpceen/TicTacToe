package com.hpceen.tictactoe

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hpceen.tictactoe.help_classes.Cluster
import com.hpceen.tictactoe.states.GameState
import com.hpceen.tictactoe.states.Turn

class GameViewModel : ViewModel() {
    var gameField: MutableLiveData<MutableList<Cluster>> = MutableLiveData(mutableListOf())
    var gameState: MutableLiveData<GameState> = MutableLiveData(GameState.Nothing)
    var currentTurn: MutableLiveData<Turn> = MutableLiveData(Turn.X)
    var allowedClusters: MutableLiveData<MutableSet<Int>> = MutableLiveData((0..8).toMutableSet())
    var finishedClusters: MutableLiveData<MutableSet<Int>> = MutableLiveData(mutableSetOf())
}