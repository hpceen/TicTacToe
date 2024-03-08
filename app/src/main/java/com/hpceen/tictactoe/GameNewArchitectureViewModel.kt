package com.hpceen.tictactoe

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameNewArchitectureViewModel : ViewModel() {
    var gameField: MutableLiveData<MutableList<Cluster>> = MutableLiveData(mutableListOf())
    var gameState: MutableLiveData<GameState> = MutableLiveData(GameState.Nothing)
    var currentTurn: MutableLiveData<Turn> = MutableLiveData(Turn.X)
    var allowedClusters: MutableLiveData<MutableSet<Int>> = MutableLiveData((0..8).toMutableSet())
    var finishedClusters: MutableLiveData<MutableSet<Int>> = MutableLiveData(mutableSetOf())
}